"use client"

import type React from "react"
import { useState, useEffect } from "react"
import { useRouter } from "next/navigation"
import { useAuth } from "@/shared/contexts/AuthContext"
import { ApiService } from "@/shared/services"
import type { RentalRequest } from "@/shared/types/rental-request"
import { RequestStatus } from "@/shared/types/rental-request"
import {
    Calendar,
    Car,
    Clock,
    Eye,
    ArrowLeft,
    CheckCircle,
    XCircle,
    AlertTriangle,
    FileText,
    User,
    DollarSign
} from "lucide-react"
import {
    getCustomerDisplayName,
    getAutomobileDisplayName,
    calculateDaysBetween,
    safeNumber,
    formatCurrency,
    formatDate
} from "@/shared/utils/type-guards"

const RentalRequestsPage: React.FC = () => {
    const router = useRouter()
    const { user } = useAuth()
    const [requests, setRequests] = useState<RentalRequest[]>([])
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState<string | null>(null)
    const [selectedRequest, setSelectedRequest] = useState<RentalRequest | null>(null)
    const [updatingStatus, setUpdatingStatus] = useState<string | null>(null)

    const isAgent = user?.userType === 'agente-empresa' || user?.userType === 'agente-banco'

    useEffect(() => {
        if (!user) {
            router.push('/')
            return
        }

        fetchRequests()
    }, [user, router])

    const fetchRequests = async () => {
        try {
            setLoading(true)
            let fetchedRequests: RentalRequest[]

            if (user?.userType === 'cliente') {
                // Para clientes, buscar apenas suas solicitações
                fetchedRequests = await ApiService.rentalRequest.getMyRequests()
            } else {
                // Para agentes, buscar todas as solicitações
                fetchedRequests = await ApiService.rentalRequest.getAllRentalRequests()
            }

            setRequests(fetchedRequests)

            // Selecionar a primeira solicitação automaticamente
            if (fetchedRequests.length > 0) {
                setSelectedRequest(fetchedRequests[0])
            }
        } catch (err) {
            console.error('Error fetching requests:', err)
            setError('Erro ao carregar solicitações.')
        } finally {
            setLoading(false)
        }
    }

    const handleStatusChange = async (requestId: string, newStatus: RequestStatus) => {
        if (!isAgent) return

        try {
            setUpdatingStatus(requestId)

            const response = await ApiService.rentalRequest.updateRequestStatus(requestId, {
                status: newStatus
            })

            // Atualizar localmente
            setRequests(prev => prev.map(req =>
                req.id === requestId ? response : req
            ))

            if (selectedRequest?.id === requestId) {
                setSelectedRequest(response)
            }

            alert(`Status atualizado para: ${getStatusLabel(newStatus)}`)
        } catch (err) {
            console.error('Error updating status:', err)
            alert('Erro ao atualizar status da solicitação')
        } finally {
            setUpdatingStatus(null)
        }
    }

    const handleGoBack = () => {
        router.push('/')
    }

    const getStatusColor = (status: string) => {
        switch (status) {
            case RequestStatus.PENDING:
                return 'bg-blue-100 text-blue-800'
            case RequestStatus.UNDER_ANALYSIS:
                return 'bg-yellow-100 text-yellow-800'
            case RequestStatus.APPROVED:
                return 'bg-green-100 text-green-800'
            case RequestStatus.REJECTED:
                return 'bg-red-100 text-red-800'
            case RequestStatus.CANCELLED:
                return 'bg-gray-100 text-gray-800'
            case RequestStatus.ACTIVE:
                return 'bg-purple-100 text-purple-800'
            case RequestStatus.COMPLETED:
                return 'bg-indigo-100 text-indigo-800'
            default:
                return 'bg-gray-100 text-gray-800'
        }
    }

    const getStatusIcon = (status: string) => {
        switch (status) {
            case RequestStatus.APPROVED:
            case RequestStatus.ACTIVE:
            case RequestStatus.COMPLETED:
                return <CheckCircle className="w-4 h-4" />
            case RequestStatus.REJECTED:
            case RequestStatus.CANCELLED:
                return <XCircle className="w-4 h-4" />
            case RequestStatus.UNDER_ANALYSIS:
                return <AlertTriangle className="w-4 h-4" />
            default:
                return <Clock className="w-4 h-4" />
        }
    }

    const getStatusLabel = (status: string) => {
        const labels = {
            [RequestStatus.PENDING]: 'Pendente',
            [RequestStatus.UNDER_ANALYSIS]: 'Em Análise',
            [RequestStatus.APPROVED]: 'Aprovado',
            [RequestStatus.REJECTED]: 'Rejeitado',
            [RequestStatus.CANCELLED]: 'Cancelado',
            [RequestStatus.ACTIVE]: 'Ativo',
            [RequestStatus.COMPLETED]: 'Concluído'
        }
        return labels[status as RequestStatus] || status
    }

    if (!user) {
        return null
    }

    if (loading) {
        return (
            <div className="min-h-screen relative flex items-center justify-center">
                {/* Vídeo de fundo */}
                <video
                    autoPlay
                    loop
                    muted
                    playsInline
                    className="fixed inset-0 w-full h-full object-cover z-0"
                >
                    <source src="/wallpaper.mp4" type="video/mp4" />
                </video>

                {/* Overlay escuro */}
                <div className="fixed inset-0 bg-black/60 z-10"></div>

                {/* Conteúdo de loading */}
                <div className="relative z-20 text-center">
                    <div className="w-12 h-12 border-4 border-white/30 border-t-white rounded-full animate-spin mx-auto mb-4"></div>
                    <div className="text-xl text-white drop-shadow-lg">Carregando solicitações...</div>
                </div>
            </div>
        )
    }

    return (
        <div className="min-h-screen relative">
            {/* Vídeo de fundo */}
            <video
                autoPlay
                loop
                muted
                playsInline
                className="fixed inset-0 w-full h-full object-cover z-0"
            >
                <source src="/wallpaper.mp4" type="video/mp4" />
            </video>

            {/* Overlay escuro */}
            <div className="fixed inset-0 bg-black/40 z-10"></div>

            {/* Conteúdo da página */}
            <div className="relative z-20">
                {/* Header */}
                <div className="bg-white/10 backdrop-blur-md border-b border-white/20">
                    <div className="container mx-auto px-4 py-6">
                        <div className="flex items-center justify-between">
                            <div className="flex items-center gap-4">
                                <button
                                    onClick={handleGoBack}
                                    className="text-white hover:bg-white/10 p-2 rounded-full transition-colors"
                                >
                                    <ArrowLeft className="w-6 h-6" />
                                </button>
                                <div>
                                    <h1 className="text-3xl font-bold text-white mb-2 drop-shadow-lg">
                                        {user.userType === 'cliente' ? 'Minhas Solicitações' : 'Gerenciar Solicitações'}
                                    </h1>
                                    <p className="text-white/90 drop-shadow-md">
                                        {user.userType === 'cliente'
                                            ? 'Acompanhe o status das suas solicitações de aluguel'
                                            : 'Visualize e gerencie todas as solicitações'
                                        }
                                    </p>
                                </div>
                            </div>
                            <div className="text-right">
                                <p className="text-white/90 drop-shadow-md">Olá, <span className="font-bold">{user.name}</span></p>
                            </div>
                        </div>
                    </div>
                </div>

                {error && (
                    <div className="bg-yellow-500/20 backdrop-blur-sm border-b border-yellow-500/30 p-4">
                        <p className="text-yellow-200 text-center">{error}</p>
                    </div>
                )}

                <main className="container mx-auto px-4 py-8">
                    <div className="grid grid-cols-1 lg:grid-cols-2 gap-8 h-[calc(100vh-12rem)]">
                        {/* Lista de Solicitações */}
                        <div className="bg-white/10 backdrop-blur-md rounded-xl border border-white/20 p-6 overflow-hidden flex flex-col">
                            <h2 className="text-xl font-bold text-white mb-4 drop-shadow-lg flex items-center gap-2">
                                <FileText className="w-5 h-5" />
                                Solicitações ({requests.length})
                            </h2>

                            {requests.length === 0 ? (
                                <div className="flex-1 flex items-center justify-center text-white/60">
                                    <div className="text-center">
                                        <Car className="w-12 h-12 mx-auto mb-4 opacity-50" />
                                        <p className="text-lg">Nenhuma solicitação encontrada</p>
                                    </div>
                                </div>
                            ) : (
                                <div className="flex-1 overflow-y-auto space-y-4 pr-2">
                                    {requests.map((request) => (
                                        <div
                                            key={request.id}
                                            className={`bg-white/5 backdrop-blur-sm border rounded-lg p-4 cursor-pointer transition-all hover:bg-white/10 ${
                                                selectedRequest?.id === request.id
                                                    ? 'border-blue-400 bg-blue-500/20'
                                                    : 'border-white/20'
                                            }`}
                                            onClick={() => setSelectedRequest(request)}
                                        >
                                            <div className="flex justify-between items-start mb-2">
                                                <div className="flex-1">
                                                    <div className="flex items-center gap-2 mb-1">
                                                        <Car className="w-4 h-4 text-white/80" />
                                                        <span className="font-medium text-white drop-shadow-sm">
                                                            {getAutomobileDisplayName(request.automobile)}
                                                        </span>
                                                    </div>
                                                    {isAgent && request.customer && (
                                                        <p className="text-sm text-white/70 mb-1 flex items-center gap-1">
                                                            <User className="w-3 h-3" />
                                                            {getCustomerDisplayName(request.customer)}
                                                        </p>
                                                    )}
                                                    <div className="flex items-center gap-2 text-sm text-white/70">
                                                        <Calendar className="w-3 h-3" />
                                                        <span>
                                                            {formatDate(request.pickupDate)} - {formatDate(request.returnDate)}
                                                        </span>
                                                    </div>
                                                </div>
                                                <div className="text-right">
                                                    <div className={`inline-flex items-center gap-1 px-2 py-1 rounded-full text-xs font-medium ${getStatusColor(request.status || 'PENDING')}`}>
                                                        {getStatusIcon(request.status || 'PENDING')}
                                                        {getStatusLabel(request.status || 'PENDING')}
                                                    </div>
                                                    {request.totalValue && (
                                                        <p className="text-sm text-white/70 mt-1">
                                                            {formatCurrency(safeNumber(request.totalValue))}
                                                        </p>
                                                    )}
                                                </div>
                                            </div>
                                        </div>
                                    ))}
                                </div>
                            )}
                        </div>

                        {/* Detalhes da Solicitação */}
                        <div className="bg-white/10 backdrop-blur-md rounded-xl border border-white/20 p-6 overflow-hidden flex flex-col">
                            {selectedRequest ? (
                                <div className="space-y-6 overflow-y-auto">
                                    <div className="flex items-center justify-between">
                                        <h3 className="text-xl font-bold text-white drop-shadow-lg">Detalhes da Solicitação</h3>
                                        <div className={`inline-flex items-center gap-2 px-3 py-2 rounded-full text-sm font-medium ${getStatusColor(selectedRequest.status || 'PENDING')}`}>
                                            {getStatusIcon(selectedRequest.status || 'PENDING')}
                                            {getStatusLabel(selectedRequest.status || 'PENDING')}
                                        </div>
                                    </div>

                                    {/* Actions for Agents */}
                                    {isAgent && (
                                        <div className="bg-white/5 backdrop-blur-sm rounded-lg p-4 border border-white/20">
                                            <h4 className="text-white font-medium mb-3">Ações</h4>
                                            <div className="flex gap-2 flex-wrap">
                                                {selectedRequest.status === RequestStatus.PENDING && (
                                                    <button
                                                        onClick={() => handleStatusChange(selectedRequest.id!, RequestStatus.UNDER_ANALYSIS)}
                                                        disabled={updatingStatus === selectedRequest.id}
                                                        className="px-3 py-2 bg-yellow-600 hover:bg-yellow-700 text-white rounded text-sm disabled:opacity-50 transition-colors"
                                                    >
                                                        {updatingStatus === selectedRequest.id ? 'Processando...' : 'Analisar'}
                                                    </button>
                                                )}
                                                {selectedRequest.status === RequestStatus.UNDER_ANALYSIS && (
                                                    <>
                                                        <button
                                                            onClick={() => handleStatusChange(selectedRequest.id!, RequestStatus.APPROVED)}
                                                            disabled={updatingStatus === selectedRequest.id}
                                                            className="px-3 py-2 bg-green-600 hover:bg-green-700 text-white rounded text-sm disabled:opacity-50 transition-colors"
                                                        >
                                                            Aprovar
                                                        </button>
                                                        <button
                                                            onClick={() => handleStatusChange(selectedRequest.id!, RequestStatus.REJECTED)}
                                                            disabled={updatingStatus === selectedRequest.id}
                                                            className="px-3 py-2 bg-red-600 hover:bg-red-700 text-white rounded text-sm disabled:opacity-50 transition-colors"
                                                        >
                                                            Rejeitar
                                                        </button>
                                                    </>
                                                )}
                                            </div>
                                        </div>
                                    )}

                                    {/* Vehicle Information */}
                                    {selectedRequest.automobile && (
                                        <div className="bg-white/5 backdrop-blur-sm rounded-lg p-4 border border-white/20">
                                            <h4 className="font-medium mb-3 flex items-center gap-2 text-white">
                                                <Car className="w-4 h-4" />
                                                Veículo
                                            </h4>
                                            <div className="text-sm text-white/80 space-y-2">
                                                <p><strong className="text-white">Modelo:</strong> {selectedRequest.automobile.brand} {selectedRequest.automobile.model}</p>
                                                <p><strong className="text-white">Ano:</strong> {selectedRequest.automobile.year}</p>
                                                {selectedRequest.automobile.dailyRate && (
                                                    <p><strong className="text-white">Valor Diário:</strong> {formatCurrency(safeNumber(selectedRequest.automobile.dailyRate))}</p>
                                                )}
                                            </div>
                                        </div>
                                    )}

                                    {/* Customer Information (for agents) */}
                                    {isAgent && selectedRequest.customer && (
                                        <div className="bg-white/5 backdrop-blur-sm rounded-lg p-4 border border-white/20">
                                            <h4 className="font-medium mb-3 text-white flex items-center gap-2">
                                                <User className="w-4 h-4" />
                                                Cliente
                                            </h4>
                                            <div className="text-sm text-white/80 space-y-2">
                                                <p><strong className="text-white">Nome:</strong> {selectedRequest.customer.name}</p>
                                                <p><strong className="text-white">Email:</strong> {selectedRequest.customer.emailContact}</p>
                                            </div>
                                        </div>
                                    )}

                                    {/* Rental Details */}
                                    <div className="bg-white/5 backdrop-blur-sm rounded-lg p-4 border border-white/20">
                                        <h4 className="font-medium mb-3 flex items-center gap-2 text-white">
                                            <Calendar className="w-4 h-4" />
                                            Período do Aluguel
                                        </h4>
                                        <div className="text-sm text-white/80 space-y-2">
                                            <p><strong className="text-white">Início:</strong> {formatDate(selectedRequest.pickupDate)}</p>
                                            <p><strong className="text-white">Término:</strong> {formatDate(selectedRequest.returnDate)}</p>
                                            <p><strong className="text-white">Duração:</strong> {
                                                calculateDaysBetween(selectedRequest.pickupDate, selectedRequest.returnDate)
                                            } dias</p>
                                            {selectedRequest.totalValue && (
                                                <p className="flex items-center gap-1">
                                                    <DollarSign className="w-3 h-3" />
                                                    <strong className="text-white">Valor Total:</strong>
                                                    <span className="text-green-300 font-bold">{formatCurrency(safeNumber(selectedRequest.totalValue))}</span>
                                                </p>
                                            )}
                                        </div>
                                    </div>

                                    {/* Additional Information */}
                                    <div className="bg-white/5 backdrop-blur-sm rounded-lg p-4 border border-white/20">
                                        <h4 className="font-medium mb-3 text-white">Informações Adicionais</h4>
                                        <div className="text-sm text-white/80 space-y-2">
                                            {selectedRequest.createdAt && (
                                                <p><strong className="text-white">Data da Solicitação:</strong> {formatDate(selectedRequest.createdAt)}</p>
                                            )}
                                            {selectedRequest.observations && (
                                                <div>
                                                    <strong className="text-white">Observações:</strong>
                                                    <div className="mt-2 bg-white/5 p-3 rounded border border-white/10">
                                                        <p className="text-white/90 leading-relaxed">{selectedRequest.observations}</p>
                                                    </div>
                                                </div>
                                            )}
                                        </div>
                                    </div>
                                </div>
                            ) : (
                                <div className="flex-1 flex items-center justify-center text-white/60">
                                    <div className="text-center">
                                        <Eye className="w-12 h-12 mx-auto mb-4 opacity-50" />
                                        <p className="text-lg">Selecione uma solicitação para ver os detalhes</p>
                                    </div>
                                </div>
                            )}
                        </div>
                    </div>
                </main>

                {/* Footer */}
                <footer className="bg-transparent py-8">
                    <div className="container mx-auto px-4 text-center">
                        <p className="text-white/60">© 2024 RentalCarSystem. Todos os direitos reservados.</p>
                    </div>
                </footer>
            </div>
        </div>
    )
}

export default RentalRequestsPage