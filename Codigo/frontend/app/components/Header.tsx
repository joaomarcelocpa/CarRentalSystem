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
        <header className="text-white shadow-sm">
            <div className="container mx-auto px-4 py-4">
                <div className="flex justify-between items-center">
                    <div className="flex items-center space-x-2">
                        <div className="w-8 h-8 bg-blue-600 rounded-full flex items-center justify-center">
                            <span className="text-white font-bold text-lg">R</span>
                        </div>
                        <h1 className="text-2xl font-bold">RentalCarSystem</h1>
                    </div>

                    <div className="flex items-center space-x-4">
                        {user ? (
                            <div className="flex items-center space-x-4">
                                <span className="text-sm">
                                    Olá, <span className="font-semibold">{user.name}</span>
                                    <span className="ml-2 px-2 py-1 bg-blue-600 text-white rounded-full text-xs">
                                        {user.userType === "cliente"
                                            ? "Cliente"
                                            : user.userType === "agente-empresa"
                                                ? "Agente Empresa"
                                                : "Agente Banco"}
                                    </span>
                                </span>
                                <button
                                    onClick={logout}
                                    className="px-4 py-2 bg-red-600 hover:bg-red-700 text-white rounded-lg transition-colors"
                                >
                                    Sair
                                </button>
                            </div>
                        ) : (
                            <div className="flex space-x-3">
                                <button
                                    onClick={onLoginClick}
                                    className="px-6 py-2 bg-blue-600 hover:bg-blue-700 text-white rounded-lg transition-colors font-medium"
                                >
                                    Login
                                </button>
                                <button
                                    onClick={onRegisterClick}
                                    className="px-6 py-2 bg-gray-300 hover:bg-gray-400 text-blue-800 rounded-lg transition-colors font-medium"
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