"use client"

import type React from "react"
import { createContext, useContext, useState, useEffect, type ReactNode } from "react"
import type { User, AuthContextType, UserType } from "@/app/interfaces/user"
import { ApiService } from "@/app/services"
import type { CustomerCreateDTO } from "@/app/types/customer"
import type { AgentResponseDTO } from "@/app/types/agent"
import type { Bank } from "@/app/types/bank"

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

    // Check for existing session on mount
    useEffect(() => {
        const checkExistingSession = () => {
            try {
                const savedUser = localStorage.getItem('rental_user')
                if (savedUser) {
                    const parsedUser = JSON.parse(savedUser)
                    setUser(parsedUser)
                }
            } catch (error) {
                console.error('Error checking existing session:', error)
                localStorage.removeItem('rental_user')
            } finally {
                setIsLoading(false)
            }
        }

        checkExistingSession()
    }, [])

    const login = async (email: string, password: string): Promise<boolean> => {
        try {
            setIsLoading(true)

            // First, try to find user in customers database
            const customers = await ApiService.customer.getAllCustomers()
            const existingCustomer = customers.find(c => c.emailContact === email)

            if (existingCustomer) {
                // User found as customer
                const newUser: User = {
                    id: existingCustomer.id,
                    name: existingCustomer.name,
                    email: existingCustomer.emailContact,
                    userType: 'cliente',
                    isLoggedIn: true,
                }
                setUser(newUser)
                localStorage.setItem('rental_user', JSON.stringify(newUser))
                return true
            }

            // If not found in customers, check for agent types by email pattern or specific logic
            // For demonstration, we'll use email domains to determine agent types
            if (email.includes('@empresa.') || email.includes('@company.') || email.includes('@corp.')) {
                const newUser: User = {
                    id: Math.random().toString(36).substr(2, 9),
                    name: email.split("@")[0],
                    email,
                    userType: 'agente-empresa',
                    isLoggedIn: true,
                }
                setUser(newUser)
                localStorage.setItem('rental_user', JSON.stringify(newUser))
                return true
            }

            if (email.includes('@banco.') || email.includes('@bank.')) {
                const newUser: User = {
                    id: Math.random().toString(36).substr(2, 9),
                    name: email.split("@")[0],
                    email,
                    userType: 'agente-banco',
                    isLoggedIn: true,
                }
                setUser(newUser)
                localStorage.setItem('rental_user', JSON.stringify(newUser))
                return true
            }

            // If no specific pattern is found, deny login
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
        localStorage.removeItem('rental_user')
    }

    const register = async (name: string, email: string, password: string, userType: UserType): Promise<boolean> => {
        try {
            setIsLoading(true)

            if (userType === 'cliente') {
                // Check if customer already exists
                const customers = await ApiService.customer.getAllCustomers()
                const existingCustomer = customers.find(c => c.emailContact === email)

                if (existingCustomer) {
                    throw new Error('Email already exists')
                }

                // Create new customer
                const customerData: CustomerCreateDTO = {
                    name,
                    emailContact: email,
                }

                const newCustomer = await ApiService.customer.createCustomer(customerData)

                const newUser: User = {
                    id: newCustomer.id,
                    name: newCustomer.name,
                    email: newCustomer.emailContact,
                    userType,
                    isLoggedIn: true,
                }

                setUser(newUser)
                localStorage.setItem('rental_user', JSON.stringify(newUser))
                return true
            }

            // For agent types, simulate registration
            if (userType === 'agente-empresa' || userType === 'agente-banco') {
                if (name && email && password) {
                    const newUser: User = {
                        id: Math.random().toString(36).substr(2, 9),
                        name,
                        email,
                        userType,
                        isLoggedIn: true,
                    }
                    setUser(newUser)
                    localStorage.setItem('rental_user', JSON.stringify(newUser))
                    return true
                }
            }

            return false
        } catch (error) {
            console.error('Registration error:', error)
            return false
        } finally {
            setIsLoading(false)
        }
    }

    const value: AuthContextType = {
        user,
        login,
        logout,
        register,
        isLoading,
    }

    return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}