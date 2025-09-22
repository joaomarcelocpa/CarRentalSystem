"use client"

import type React from "react"
import { createContext, useContext, useState, useEffect, type ReactNode } from "react"
import type { User, AuthContextType, UserType } from "@/shared/interfaces/user"
import { ApiService } from "@/shared/services"
import type { CustomerCreateDTO } from "@/shared/types/customer"

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
        const checkExistingSession = () => {
            try {
                const savedUser = localStorage.getItem(CURRENT_USER_STORAGE_KEY)
                if (savedUser) {
                    const parsedUser = JSON.parse(savedUser)
                    setUser(parsedUser)
                }
            } catch (error) {
                console.error('Error checking existing session:', error)
                localStorage.removeItem(CURRENT_USER_STORAGE_KEY)
            } finally {
                setIsLoading(false)
            }
        }

        checkExistingSession()
    }, [])

    const login = async (email: string, password: string): Promise<boolean> => {
        try {
            setIsLoading(true)

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