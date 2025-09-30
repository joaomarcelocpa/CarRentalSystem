"use client"

import type React from "react"
import { createContext, useContext, useState, useEffect, type ReactNode } from "react"
import type { User, AuthContextType, UserType } from "@/shared/interfaces/user"
import { ApiService } from "@/shared/services"
import type { CustomerCreateDTO } from "@/shared/types/customer"
import type { UserCreateDTO, UserResponseDTO, UserRole } from "@/shared/types/user"

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

    const CURRENT_USER_STORAGE_KEY = 'rental_current_user'

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
                        localStorage.removeItem(CURRENT_USER_STORAGE_KEY)
                        ApiService.auth.removeToken()
                    }
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
            const loginResponse = await ApiService.auth.login({
                username: email,
                password: password
            })

            const loggedUser: User = {
                id: loginResponse.id ?? loginResponse.username,
                name: loginResponse.username,
                email: loginResponse.email,
                userType: roleToUserType(loginResponse.role),
                isLoggedIn: true,
            }

            ApiService.auth.setToken(loginResponse.token)
            setUser(loggedUser)
            saveCurrentUser(loggedUser)
            return true
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

            const userCreateDTO: UserCreateDTO = {
                username: name.trim(),
                email: email.trim(),
                password: password,
                role: userTypeToRole(userType)
            }

            const loginResponse = await ApiService.auth.register(userCreateDTO)

            const newUser: User = {
                id: loginResponse.id ?? loginResponse.username,
                name: loginResponse.username,
                email: loginResponse.email,
                userType: roleToUserType(loginResponse.role),
                isLoggedIn: true,
            }

            ApiService.auth.setToken(loginResponse.token)

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
                }
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

    const value: AuthContextType = {
        user,
        login,
        logout,
        register,
        isLoading
    }

    return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}