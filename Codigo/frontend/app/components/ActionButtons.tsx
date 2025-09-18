"use client"

import type React from "react"
import { useAuth } from "../contexts/AuthContext"
import type { UserType } from "../types/user"

const ActionButtons: React.FC = () => {
    const { user } = useAuth()

    if (!user) {
        return (
            <div className="text-center py-8">
                <p className="text-white/80 text-lg">Faça login para acessar as funcionalidades do sistema</p>
            </div>
        )
    }

    const getButtonsForUserType = (userType: UserType) => {
        const baseButtons = [{ id: "edit-profile", label: "Editar Dados Pessoais", icon: "👤" }]

        switch (userType) {
            case "cliente":
                return [
                    { id: "rent-car", label: "Realizar Aluguel", icon: "🚗" },
                    { id: "view-orders", label: "Visualizar Pedidos", icon: "📋" },
                    ...baseButtons,
                ]

            case "agente-empresa":
                return [
                    { id: "evaluate-orders", label: "Avaliar Pedidos", icon: "✅" },
                    { id: "modify-orders", label: "Modificar Pedidos", icon: "✏️" },
                    ...baseButtons,
                ]

            case "agente-banco":
                return [
                    { id: "evaluate-orders", label: "Avaliar Pedidos", icon: "✅" },
                    { id: "modify-orders", label: "Modificar Pedidos", icon: "✏️" },
                    { id: "credit-contract", label: "Conceder Contrato de Crédito", icon: "💳" },
                    ...baseButtons,
                ]

            default:
                return baseButtons
        }
    }

    const buttons = getButtonsForUserType(user.userType)

    const handleButtonClick = (buttonId: string) => {
        // Por enquanto apenas log - futuramente será implementada a navegação
        console.log(`Clicou no botão: ${buttonId}`)
        alert(`Funcionalidade "${buttonId}" será implementada em breve!`)
    }

    return (
        <div className="w-full max-w-4xl mx-auto">
            <div className="text-center mb-8">
                <h2 className="text-3xl font-bold text-white mb-2 drop-shadow-lg">Painel de Controle</h2>
                <p className="text-white/80 drop-shadow-md">Bem-vindo ao seu painel, {user.name}!</p>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                {buttons.map((button) => (
                    <button
                        key={button.id}
                        onClick={() => handleButtonClick(button.id)}
                        className="group bg-white/10 backdrop-blur-md hover:bg-white/20 border border-white/20 rounded-xl p-6 transition-all duration-200 hover:shadow-lg hover:scale-105"
                    >
                        <div className="text-center">
                            <div className="text-4xl mb-4 group-hover:scale-110 transition-transform">{button.icon}</div>
                            <h3 className="text-lg font-semibold text-white group-hover:text-blue-200">
                                {button.label}
                            </h3>
                        </div>
                    </button>
                ))}
            </div>
        </div>
    )
}

export default ActionButtons