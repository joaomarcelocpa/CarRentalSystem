/* eslint-disable @typescript-eslint/no-explicit-any */
// app/components/DebugPanel.tsx (opcional para desenvolvimento)
"use client"

import type React from "react"
import { useState } from "react"
import { useAuth } from "@/shared/contexts/AuthContext"
import { Trash2, Users, Eye, EyeOff } from "lucide-react"

interface DebugPanelProps {
    show?: boolean
}

const DebugPanel: React.FC<DebugPanelProps> = ({ show = false }) => {
    const { user, clearAllData, getRegisteredUsers } = useAuth() as any
    const [isVisible, setIsVisible] = useState(show)
    const [showUsers, setShowUsers] = useState(false)

    if (!isVisible) {
        return (
            <button
                onClick={() => setIsVisible(true)}
                className="fixed bottom-4 right-4 bg-gray-800 text-white p-2 rounded-full shadow-lg hover:bg-gray-700 transition-colors z-50"
                title="Mostrar painel de debug"
            >
                <Eye className="w-4 h-4" />
            </button>
        )
    }

    const registeredUsers = getRegisteredUsers ? getRegisteredUsers() : []

    return (
        <div className="fixed bottom-4 right-4 bg-white border border-gray-300 rounded-lg shadow-lg p-4 max-w-sm w-80 z-50">
            <div className="flex justify-between items-center mb-3">
                <h3 className="text-sm font-medium text-gray-900">Debug Panel</h3>
                <button
                    onClick={() => setIsVisible(false)}
                    className="text-gray-400 hover:text-gray-600"
                >
                    <EyeOff className="w-4 h-4" />
                </button>
            </div>

            {/* Usuário atual */}
            <div className="mb-4 p-3 bg-blue-50 rounded-lg">
                <p className="text-xs font-medium text-blue-800 mb-1">Usuário Atual</p>
                {user ? (
                    <div className="text-xs text-blue-700 space-y-1">
                        <p><strong>Nome:</strong> {user.name}</p>
                        <p><strong>Email:</strong> {user.email}</p>
                        <p><strong>Tipo:</strong> {user.userType}</p>
                        <p><strong>ID:</strong> {user.id}</p>
                    </div>
                ) : (
                    <p className="text-xs text-blue-700">Nenhum usuário logado</p>
                )}
            </div>

            {/* Usuários cadastrados */}
            <div className="mb-4">
                <button
                    onClick={() => setShowUsers(!showUsers)}
                    className="flex items-center gap-2 text-sm font-medium text-gray-700 mb-2"
                >
                    <Users className="w-4 h-4" />
                    Usuários Cadastrados ({registeredUsers.length})
                </button>

                {showUsers && (
                    <div className="max-h-40 overflow-y-auto space-y-2">
                        {registeredUsers.length === 0 ? (
                            <p className="text-xs text-gray-500">Nenhum usuário cadastrado</p>
                        ) : (
                            registeredUsers.map((regUser: any) => (
                                <div key={regUser.id} className="p-2 bg-gray-50 rounded text-xs">
                                    <p><strong>{regUser.name}</strong></p>
                                    <p className="text-gray-600">{regUser.email}</p>
                                    <p className="text-blue-600">{regUser.userType}</p>
                                </div>
                            ))
                        )}
                    </div>
                )}
            </div>

            {/* Ações de debug */}
            <div className="flex gap-2">
                <button
                    onClick={() => {
                        if (clearAllData && confirm('Limpar todos os dados? Esta ação não pode ser desfeita.')) {
                            clearAllData()
                        }
                    }}
                    className="flex items-center gap-1 px-3 py-1 bg-red-600 text-white text-xs rounded hover:bg-red-700 transition-colors"
                >
                    <Trash2 className="w-3 h-3" />
                    Limpar Dados
                </button>
            </div>

            {/* Instruções */}
            <div className="mt-4 p-3 bg-yellow-50 border border-yellow-200 rounded-lg">
                <p className="text-xs font-medium text-yellow-800 mb-1">Instruções</p>
                <ul className="text-xs text-yellow-700 space-y-1">
                    <li>• Clientes: email comum (ex: user@gmail.com)</li>
                    <li>• Empresas: @empresa.com ou @company.com</li>
                    <li>• Bancos: @banco.com ou @bank.com</li>
                </ul>
            </div>
        </div>
    )
}

export default DebugPanel