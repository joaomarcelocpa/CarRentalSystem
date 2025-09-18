"use client"

import type React from "react"
import { useAuth } from "../contexts/AuthContext"

interface HeaderProps {
    onLoginClick: () => void
    onRegisterClick: () => void
}

const Header: React.FC<HeaderProps> = ({ onLoginClick, onRegisterClick }) => {
    const { user, logout } = useAuth()

    return (
        <header className="bg-primary text-primary-foreground shadow-lg">
            <div className="container mx-auto px-4 py-4">
                <div className="flex justify-between items-center">
                    <div className="flex items-center space-x-2">
                        <div className="w-8 h-8 bg-primary-foreground rounded-full flex items-center justify-center">
                            <span className="text-primary font-bold text-lg">R</span>
                        </div>
                        <h1 className="text-2xl font-bold">RentalCarSystem</h1>
                    </div>

                    <div className="flex items-center space-x-4">
                        {user ? (
                            <div className="flex items-center space-x-4">
                <span className="text-sm">
                  Olá, <span className="font-semibold">{user.name}</span>
                  <span className="ml-2 px-2 py-1 bg-primary-foreground text-primary rounded-full text-xs">
                    {user.userType === "cliente"
                        ? "Cliente"
                        : user.userType === "agente-empresa"
                            ? "Agente Empresa"
                            : "Agente Banco"}
                  </span>
                </span>
                                <button
                                    onClick={logout}
                                    className="px-4 py-2 bg-destructive text-destructive-foreground rounded-lg hover:bg-destructive/90 transition-colors"
                                >
                                    Sair
                                </button>
                            </div>
                        ) : (
                            <div className="flex space-x-3">
                                <button
                                    onClick={onLoginClick}
                                    className="px-6 py-2 bg-primary-foreground text-primary rounded-lg hover:bg-primary-foreground/90 transition-colors font-medium"
                                >
                                    Login
                                </button>
                                <button
                                    onClick={onRegisterClick}
                                    className="px-6 py-2 border-2 border-primary-foreground text-primary-foreground rounded-lg hover:bg-primary-foreground hover:text-primary transition-colors font-medium"
                                >
                                    Cadastrar-se
                                </button>
                            </div>
                        )}
                    </div>
                </div>
            </div>
        </header>
    )
}

export default Header
