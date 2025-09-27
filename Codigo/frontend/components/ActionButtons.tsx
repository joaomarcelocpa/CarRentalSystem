// app/components/ActionButtons.tsx
"use client"

import type React from "react"
import { useRouter } from "next/navigation"
import { useAuth } from "@/shared/contexts/AuthContext"
import type { UserType } from "@/shared/interfaces/user"
import { Car, FileText, User, CheckCircle, Edit, CreditCard, Settings } from "lucide-react"

const ActionButtons: React.FC = () => {
    const { user } = useAuth()
    const router = useRouter()

    if (!user) {
        return (
            <div className="text-center py-8">
                <p className="text-white/80 text-lg">Faça login para acessar as funcionalidades do sistema</p>
            </div>
        )
    }

    const handleRentCar = () => {
        router.push('/car-selection')
    }

    const handleEditProfile = () => {
        router.push('/edit-profile')
    }

    const handleViewOrders = () => {
        router.push('/rental-requests')
    }

    const handleCreditContract = () => {
        alert("Funcionalidade de contrato de crédito será implementada em breve!")
    }

    const handleManageAutomobiles = () => {
        router.push('/automobile-management')
    }

    const getButtonsForUserType = (userType: UserType) => {
        const baseButtons = [
            {
                id: "edit-profile",
                label: "Editar Dados Pessoais",
                icon: User,
                action: handleEditProfile
            }
        ]

        switch (userType) {
            case "cliente":
                return [
                    {
                        id: "rent-car",
                        label: "Realizar Aluguel",
                        icon: Car,
                        action: handleRentCar
                    },
                    {
                        id: "view-orders",
                        label: "Visualizar Pedidos",
                        icon: FileText,
                        action: handleViewOrders
                    },
                    ...baseButtons,
                ]

            case "agente-empresa":
                return [
                    {
                        id: "evaluate-orders",
                        label: "Avaliar Pedidos",
                        icon: CheckCircle,
                        action: handleViewOrders
                    },
                    {
                        id: "modify-orders",
                        label: "Modificar Pedidos",
                        icon: Edit,
                        action: handleViewOrders
                    },
                    {
                        id: "manage-automobiles",
                        label: "Gerenciar Veículos",
                        icon: Settings,
                        action: handleManageAutomobiles
                    },
                    ...baseButtons,
                ]

            case "agente-banco":
                return [
                    {
                        id: "evaluate-orders",
                        label: "Avaliar Pedidos",
                        icon: CheckCircle,
                        action: handleViewOrders
                    },
                    {
                        id: "modify-orders",
                        label: "Modificar Pedidos",
                        icon: Edit,
                        action: handleViewOrders
                    },
                    {
                        id: "manage-automobiles",
                        label: "Gerenciar Veículos",
                        icon: Settings,
                        action: handleManageAutomobiles
                    },
                    {
                        id: "credit-contract",
                        label: "Conceder Contrato de Crédito",
                        icon: CreditCard,
                        action: handleCreditContract
                    },
                    ...baseButtons,
                ]

            default:
                return baseButtons
        }
    }

    const buttons = getButtonsForUserType(user.userType)

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
                            onClick={button.action}
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