"use client"

import type React from "react"
import { createContext, useContext, useState, useEffect, type ReactNode } from "react"
import type { User, AuthContextType, UserType } from "@/shared/interfaces/user"
import { ApiService } from "@/shared/services"
import type { CustomerCreateDTO } from "@/shared/types/customer"
import type { UserCreateDTO, UserResponseDTO, UserRole } from "@/shared/types/user"

// Interface para dados de usuários no localStorage
interface StoredUser {
    id: string
    name: string
    email: string
    userType: UserType
    password: string // Armazenado apenas para demo - em produção, usar hash
    isLoggedIn: boolean
}

const AuthContext = createContext<AuthContextType | undefined>(undefined)

// Função para converter UserType para UserRole
const userTypeToRole = (userType: UserType): UserRole => {
    switch (userType) {
        case 'cliente':
            return 'CUSTOMER';
        case 'agente-empresa':
            return 'AGENT_COMPANY';
        case 'agente-banco':
            return 'AGENT_BANK';
        default:
            throw new Error(`Invalid user type: ${userType}`);
    }
}

// Função para converter UserRole para UserType
const roleToUserType = (role: UserRole): UserType => {
    switch (role) {
        case 'CUSTOMER':
            return 'cliente';
        case 'AGENT_COMPANY':
            return 'agente-empresa';
        case 'AGENT_BANK':
            return 'agente-banco';
        default:
            throw new Error(`Invalid role: ${role}`);
    }
}

export const useAuth = () => {
    const context = useContext(AuthContext)
    if (context === undefined) {
        throw new Error("useAuth must be used within an AuthProvider")
    }
    return context
}

interface AuthProviderProps {
    children: ReactNode
}

export const AuthProvider: React.FC<AuthProviderProps> = ({ children }) => {
    const [user, setUser] = useState<User | null>(null)
    const [isLoading, setIsLoading] = useState(true)

    // Chaves para o localStorage
    const USERS_STORAGE_KEY = 'rental_system_users'
    const CURRENT_USER_STORAGE_KEY = 'rental_current_user'

    // Função para obter todos os usuários do localStorage
    const getAllUsers = (): StoredUser[] => {
        try {
            const users = localStorage.getItem(USERS_STORAGE_KEY)
            return users ? JSON.parse(users) : []
        } catch (error) {
            console.error('Error reading users from localStorage:', error)
            return []
        }
    }

    // Função para salvar todos os usuários no localStorage
    const saveAllUsers = (users: StoredUser[]) => {
        try {
            localStorage.setItem(USERS_STORAGE_KEY, JSON.stringify(users))
        } catch (error) {
            console.error('Error saving users to localStorage:', error)
        }
    }

    // Função para salvar usuário atual
    const saveCurrentUser = (user: User) => {
        try {
            localStorage.setItem(CURRENT_USER_STORAGE_KEY, JSON.stringify(user))
        } catch (error) {
            console.error('Error saving current user:', error)
        }
    }

    // Verificar sessão existente no mount
    useEffect(() => {
        const checkExistingSession = async () => {
            try {
                const savedUser = localStorage.getItem(CURRENT_USER_STORAGE_KEY)
                const token = ApiService.auth.getToken()
                
                if (savedUser && token) {
                    // Verificar se o token ainda é válido tentando buscar dados do usuário
                    try {
                        const currentUser = await ApiService.auth.getCurrentUser()
                        const parsedUser = JSON.parse(savedUser)
                        
                        // Atualizar dados do usuário com informações do backend
                        const updatedUser: User = {
                            id: currentUser.id,
                            name: currentUser.username,
                            email: currentUser.email,
                            userType: roleToUserType(currentUser.role),
                            isLoggedIn: true,
                        }
                        
                        setUser(updatedUser)
                        saveCurrentUser(updatedUser)
                    } catch (tokenError) {
                        console.warn('Token invalid, clearing session:', tokenError)
                        // Token inválido, limpar sessão
                        localStorage.removeItem(CURRENT_USER_STORAGE_KEY)
                        ApiService.auth.removeToken()
                    }
                } else if (savedUser) {
                    // Usuário salvo mas sem token (fallback para localStorage)
                    const parsedUser = JSON.parse(savedUser)
                    setUser(parsedUser)
                }
            } catch (error) {
                console.error('Error checking existing session:', error)
                localStorage.removeItem(CURRENT_USER_STORAGE_KEY)
                ApiService.auth.removeToken()
            } finally {
                setIsLoading(false)
            }
        }

        checkExistingSession()
    }, [])

    const login = async (email: string, password: string): Promise<boolean> => {
        try {
            setIsLoading(true)

            // Tentar login no backend primeiro
            try {
                const loginResponse = await ApiService.auth.login({
                    username: email, // O backend usa username, mas estamos passando email
                    password: password
                })

                const loggedUser: User = {
                    id: Math.random().toString(36).substr(2, 9) + Date.now().toString(36), // Gerar ID temporário
                    name: loginResponse.username,
                    email: loginResponse.email,
                    userType: roleToUserType(loginResponse.role),
                    isLoggedIn: true,
                }

                // Salvar token
                ApiService.auth.setToken(loginResponse.token)

                setUser(loggedUser)
                saveCurrentUser(loggedUser)
                return true
            } catch (backendError) {
                console.warn('Backend login failed, trying local storage:', backendError)
                
                // Fallback para localStorage se o backend falhar
                const allUsers = getAllUsers()
                const foundUser = allUsers.find(u => u.email.toLowerCase() === email.toLowerCase() && u.password === password)

                if (foundUser) {
                    const loggedUser: User = {
                        id: foundUser.id,
                        name: foundUser.name,
                        email: foundUser.email,
                        userType: foundUser.userType,
                        isLoggedIn: true,
                    }

                    setUser(loggedUser)
                    saveCurrentUser(loggedUser)
                    return true
                }

                return false
            }
        } catch (error) {
            console.error('Login error:', error)
            return false
        } finally {
            setIsLoading(false)
        }
    }

    const logout = () => {
        setUser(null)
        localStorage.removeItem(CURRENT_USER_STORAGE_KEY)
        ApiService.auth.removeToken()
    }

    const register = async (name: string, email: string, password: string, userType: UserType): Promise<boolean> => {
        try {
            setIsLoading(true)

            if (!name.trim() || !email.trim() || !password.trim()) {
                throw new Error('Todos os campos são obrigatórios')
            }

            if (password.length < 6) {
                throw new Error('A senha deve ter pelo menos 6 caracteres')
            }

            if (!isValidEmail(email)) {
                throw new Error('Formato de email inválido')
            }

            // Tentar registro no backend primeiro
            try {
                const userCreateDTO: UserCreateDTO = {
                    username: name.trim(), // Usando name como username
                    email: email.trim(),
                    password: password,
                    role: userTypeToRole(userType)
                }

                const userResponse = await ApiService.auth.register(userCreateDTO)

                const newUser: User = {
                    id: userResponse.id,
                    name: userResponse.username,
                    email: userResponse.email,
                    userType: roleToUserType(userResponse.role),
                    isLoggedIn: true,
                }

                // Se for cliente, criar também o registro específico no Customer
                if (userType === 'cliente') {
                    try {
                        const customerData: CustomerCreateDTO = {
                            name: name.trim(),
                            emailContact: email.trim(),
                        }
                        await ApiService.customer.createCustomer(customerData)
                    } catch (customerError) {
                        console.warn('Error creating customer record:', customerError)
                        // Não falha o registro se não conseguir criar o customer específico
                    }
                }

                setUser(newUser)
                saveCurrentUser(newUser)
                return true
            } catch (backendError) {
                console.warn('Backend registration failed, trying local storage:', backendError)
                
                // Fallback para localStorage se o backend falhar
                const allUsers = getAllUsers()
                const existingUser = allUsers.find(u => u.email.toLowerCase() === email.toLowerCase())

                if (existingUser) {
                    throw new Error('Email já está em uso')
                }

                let userId = Math.random().toString(36).substr(2, 9) + Date.now().toString(36)

                if (userType === 'cliente') {
                    try {
                        const customerData: CustomerCreateDTO = {
                            name: name.trim(),
                            emailContact: email.trim(),
                        }

                        const createdCustomer = await ApiService.customer.createCustomer(customerData)
                        userId = createdCustomer.id
                    } catch (error) {
                        console.error('Error creating customer in backend, using local ID:', error)
                    }
                }

                const newStoredUser: StoredUser = {
                    id: userId,
                    name: name.trim(),
                    email: email.trim().toLowerCase(),
                    userType,
                    password,
                    isLoggedIn: false
                }

                const updatedUsers = [...allUsers, newStoredUser]
                saveAllUsers(updatedUsers)

                const newUser: User = {
                    id: newStoredUser.id,
                    name: newStoredUser.name,
                    email: newStoredUser.email,
                    userType: newStoredUser.userType,
                    isLoggedIn: true,
                }

                setUser(newUser)
                saveCurrentUser(newUser)
                return true
            }
        } catch (error) {
            console.error('Registration error:', error)
            throw error
        } finally {
            setIsLoading(false)
        }
    }

    const isValidEmail = (email: string): boolean => {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
        return emailRegex.test(email)
    }

    const clearAllData = () => {
        localStorage.removeItem(USERS_STORAGE_KEY)
        localStorage.removeItem(CURRENT_USER_STORAGE_KEY)
        setUser(null)
    }

    const getRegisteredUsers = () => {
        return getAllUsers().map(user => ({
            id: user.id,
            name: user.name,
            email: user.email,
            userType: user.userType
        }))
    }

    const addTestUsers = () => {
        const testUsers: StoredUser[] = [
            {
                id: 'test-cliente-1',
                name: 'João Cliente',
                email: 'cliente@teste.com',
                userType: 'cliente',
                password: '123456',
                isLoggedIn: false
            },
            {
                id: 'test-empresa-1',
                name: 'Maria Empresa',
                email: 'agente@teste.com',
                userType: 'agente-empresa',
                password: '123456',
                isLoggedIn: false
            },
            {
                id: 'test-banco-1',
                name: 'Pedro Banco',
                email: 'banco@teste.com',
                userType: 'agente-banco',
                password: '123456',
                isLoggedIn: false
            }
        ]

        const existingUsers = getAllUsers()
        const usersToAdd = testUsers.filter(testUser =>
            !existingUsers.some(existing => existing.email === testUser.email)
        )

        if (usersToAdd.length > 0) {
            const updatedUsers = [...existingUsers, ...usersToAdd]
            saveAllUsers(updatedUsers)
            console.log(`Adicionados ${usersToAdd.length} usuários de teste`)
        }
    }

    const value: AuthContextType = {
        user,
        login,
        logout,
        register,
        isLoading,
        clearAllData,
        getRegisteredUsers,
        addTestUsers
    } as AuthContextType & {
        clearAllData: () => void
        getRegisteredUsers: () => any[]
        addTestUsers: () => void
    }

    return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}