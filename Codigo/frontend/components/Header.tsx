"use client"

import type React from "react"
import { useAuth } from "@/shared/contexts/AuthContext"
import { LogOut } from "lucide-react"

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
                        <h1 className="text-2xl font-bold">RentalCarSystem</h1>
                    </div>

                    <div className="flex items-center space-x-4">
                        {user ? (
                            <div className="flex items-center space-x-4">
                                <span className="text-lg">
                                    Olá, <span className="font-bold">{user.name}</span>
                                </span>
                                <button
                                    onClick={logout}
                                    className="flex items-center gap-2 px-4 py-2 bg-red-600 hover:bg-red-700 text-white rounded-full transition-colors text-sm"
                                >
                                    <LogOut className="w-4 h-4" />
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