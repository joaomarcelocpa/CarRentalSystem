"use client"

import type React from "react"
import { useAuth } from "../contexts/AuthContext"
import type { UserType } from "@/app/interfaces/user"
import { Car, FileText, User, CheckCircle, Edit, CreditCard } from "lucide-react"

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
        const baseButtons = [{ id: "edit-profile", label: "Editar Dados Pessoais", icon: User }]

        switch (userType) {
            case "cliente":
                return [
                    { id: "rent-car", label: "Realizar Aluguel", icon: Car },
                    { id: "view-orders", label: "Visualizar Pedidos", icon: FileText },
                    ...baseButtons,
                ]

            case "agente-empresa":
                return [
                    { id: "evaluate-orders", label: "Avaliar Pedidos", icon: CheckCircle },
                    { id: "modify-orders", label: "Modificar Pedidos", icon: Edit },
                    ...baseButtons,
                ]

            case "agente-banco":
                return [
                    { id: "evaluate-orders", label: "Avaliar Pedidos", icon: CheckCircle },
                    { id: "modify-orders", label: "Modificar Pedidos", icon: Edit },
                    { id: "credit-contract", label: "Conceder Contrato de Crédito", icon: CreditCard },
                    ...baseButtons,
                ]

            default:
                return baseButtons
        }
    }

    const buttons = getButtonsForUserType(user.userType)

    const handleButtonClick = (buttonId: string) => {
        console.log(`Clicou no botão: ${buttonId}`)
        alert(`Funcionalidade "${buttonId}" será implementada em breve!`)
    }

    return (
        <div className="w-full max-w-5xl mx-auto">
            <div className="text-center mb-8">
                <h2 className="text-3xl font-bold text-white mb-2 drop-shadow-lg">Painel de Controle</h2>
                <p className="text-white/80 drop-shadow-md">Bem-vindo ao seu painel, {user.name}!</p>
            </div>

            <div className="flex flex-wrap justify-center gap-6">
                {buttons.map((button) => {
                    const IconComponent = button.icon
                    return (
                        <button
                            key={button.id}
                            onClick={() => handleButtonClick(button.id)}
                            className="group bg-white/10 backdrop-blur-md hover:bg-white/20 border border-white/20 rounded-xl p-6 transition-all duration-200 hover:shadow-lg hover:scale-105 w-64 sm:w-72"
                        >
                            <div className="text-center">
                                <div className="text-4xl mb-4 group-hover:scale-110 transition-transform flex justify-center">
                                    <IconComponent className="w-10 h-10 text-white" />
                                </div>
                                <h3 className="text-lg font-semibold text-white group-hover:text-blue-200">
                                    {button.label}
                                </h3>
                            </div>
                        </button>
                    )
                })}
            </div>
        </div>
    )
}

export default ActionButtons