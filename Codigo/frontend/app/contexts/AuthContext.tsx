"use client"

import type React from "react"
import { createContext, useContext, useState, type ReactNode } from "react"
import type { User, AuthContextType, UserType } from "@/app/interfaces/user"

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

    const login = async (email: string, password: string, userType: UserType): Promise<boolean> => {
        // Simulação de login - em produção seria uma chamada à API
        if (email && password) {
            const newUser: User = {
                id: Math.random().toString(36).substr(2, 9),
                name: email.split("@")[0],
                email,
                userType,
                isLoggedIn: true,
            }
            setUser(newUser)
            return true
        }
        return false
    }

    const logout = () => {
        setUser(null)
    }

    const register = async (name: string, email: string, password: string, userType: UserType): Promise<boolean> => {
        // Simulação de registro - em produção seria uma chamada à API
        if (name && email && password) {
            const newUser: User = {
                id: Math.random().toString(36).substr(2, 9),
                name,
                email,
                userType,
                isLoggedIn: true,
            }
            setUser(newUser)
            return true
        }
        return false
    }

    const value: AuthContextType = {
        user,
        login,
        logout,
        register,
    }

    return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}
