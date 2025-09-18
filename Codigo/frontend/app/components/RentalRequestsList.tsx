// app/components/RentalRequestsList.tsx
"use client"

import type React from "react"
import { useState, useEffect } from "react"
import { useAuth } from "../contexts/AuthContext"
import { ApiService } from "../services"
import type { RentalRequest } from "../types/rental-request"
import { RequestStatus } from "../types/rental-request"
import { Calendar, Car, Clock, DollarSign, Eye, X, CheckCircle, XCircle, AlertTriangle } from "lucide-react"
import {
    getCustomerDisplayName,
    getAutomobileDisplayName,
    formatCurrency,
    formatDate,
    getStatusLabel,
    calculateDaysBetween,
    safeNumber
} from "../utils/type-guards"

interface RentalRequestsListProps {
    onClose: () => void
    userType?: 'customer' | 'agent'
}

const RentalRequestsList: React.FC<RentalRequestsListProps> = ({ onClose, userType = 'customer' }) => {
    const { user } = useAuth()
    const [requests, setRequests] = useState<RentalRequest[]>([])
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState<string | null>(null)
    const [selectedRequest, setSelectedRequest] = useState<RentalRequest | null>(null)
    const [updatingStatus, setUpdatingStatus] = useState<string | null>(null)

    useEffect(() => {
        fetchRequests()
    }, [])

    const fetchRequests = async () => {
        try {
            setLoading(true)
            let fetchedRequests: RentalRequest[]

            if (userType === 'customer' && user) {
                // For customers, get only their requests
                fetchedRequests = await ApiService.rentalRequest.getRentalRequestsByCustomer(user.id)
            } else {
                // For agents, get all requests
                fetchedRequests = await ApiService.rentalRequest.getAllRentalRequests()
            }

            setRequests(fetchedRequests)
        } catch (err) {
            console.error('Error fetching requests:', err)
            setError('Erro ao carregar solicitações')
        } finally {
            setLoading(false)
        }
    }

    const handleStatusChange = async (requestId: string, newStatus: RequestStatus) => {
        if (!user || user.userType === 'cliente') return

        try {
            setUpdatingStatus(requestId)
            const updatedRequest = await ApiService.rentalRequest.updateRentalRequestStatus(requestId, newStatus)

            if (updatedRequest) {
                setRequests(prev => prev.map(req =>
                    req.id === requestId ? updatedRequest : req
                ))

                if (selectedRequest?.id === requestId) {
                    setSelectedRequest(updatedRequest)
                }
            }
        } catch (err) {
            console.error('Error updating status:', err)
            alert('Erro ao atualizar status da solicitação')
        } finally {
            setUpdatingStatus(null)
        }
    }

    const getStatusColor = (status: string) => {
        switch (status) {
            case RequestStatus.CREATED:
                return 'bg-blue-100 text-blue-800'
            case RequestStatus.UNDER_ANALYSIS:
                return 'bg-yellow-100 text-yellow-800'
            case RequestStatus.APPROVED:
                return 'bg-green-100 text-green-800'
            case RequestStatus.REJECTED:
                return 'bg-red-100 text-red-800'
            case RequestStatus.CANCELLED:
                return 'bg-gray-100 text-gray-800'
            case RequestStatus.EXECUTED:
                return 'bg-purple-100 text-purple-800'
            default:
                return 'bg-gray-100 text-gray-800'
        }
    }

    const getStatusIcon = (status: string) => {
        switch (status) {
            case RequestStatus.APPROVED:
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
    const formatCurrency = (value: number) => {
        return new Intl.NumberFormat('pt-BR', {
            style: 'currency',
            currency: 'BRL'
        }).format(value)
    }

    const formatDate = (dateString: string) => {
        return new Date(dateString).toLocaleDateString('pt-BR')
    }

    const getStatusLabel = (status: string) => {
        const labels = {
            [RequestStatus.CREATED]: 'Criada',
            [RequestStatus.UNDER_ANALYSIS]: 'Em Análise',
            [RequestStatus.APPROVED]: 'Aprovada',
            [RequestStatus.REJECTED]: 'Rejeitada',
            [RequestStatus.CANCELLED]: 'Cancelada',
            [RequestStatus.EXECUTED]: 'Executada'
        }
        return labels[status as RequestStatus] || status
    }

    if (loading) {
        return (
            <div className="fixed inset-0 bg-black/50 flex items-center justify-center p-4 z-50">
                <div className="bg-white rounded-2xl shadow-2xl w-full max-w-4xl h-96 flex items-center justify-center">
                    <div className="text-xl">Carregando solicitações...</div>
                </div>
            </div>
        )
    }

    return (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center p-4 z-50">
            <div className="bg-white rounded-2xl shadow-2xl w-full max-w-6xl max-h-[90vh] overflow-hidden">
                {/* Header */}
                <div className="bg-gradient-to-r from-blue-600 to-blue-700 text-white p-6">
                    <div className="flex justify-between items-center">
                        <div>
                            <h2 className="text-2xl font-bold">
                                {userType === 'customer' ? 'Minhas Solicitações' : 'Gerenciar Solicitações'}
                            </h2>
                            <p className="text-blue-100">
                                {userType === 'customer'
                                    ? 'Acompanhe o status das suas solicitações de aluguel'
                                    : 'Visualize e gerencie todas as solicitações'
                                }
                            </p>
                        </div>
                        <button
                            onClick={onClose}
                            className="text-white hover:bg-white/10 p-2 rounded-full transition-colors"
                        >
                            <X className="w-6 h-6" />
                        </button>
                    </div>
                </div>

                <div className="flex h-[calc(90vh-8rem)]">
                    {/* Requests List */}
                    <div className="w-1/2 p-6 border-r overflow-y-auto">
                        {error && (
                            <div className="mb-4 p-4 bg-red-50 border border-red-200 rounded-lg">
                                <p className="text-red-600">{error}</p>
                            </div>
                        )}

                        {requests.length === 0 ? (
                            <div className="text-center py-8 text-gray-500">
                                <Car className="w-12 h-12 mx-auto mb-4 opacity-50" />
                                <p>Nenhuma solicitação encontrada</p>
                            </div>
                        ) : (
                            <div className="space-y-4">
                                {requests.map((request) => (
                                    <div
                                        key={request.id}
                                        className={`border rounded-lg p-4 cursor-pointer transition-all hover:shadow-md ${
                                            selectedRequest?.id === request.id
                                                ? 'border-blue-500 bg-blue-50'
                                                : 'border-gray-200 hover:border-gray-300'
                                        }`}
                                        onClick={() => setSelectedRequest(request)}
                                    >
                                        <div className="flex justify-between items-start mb-2">
                                            <div className="flex-1">
                                                <div className="flex items-center gap-2 mb-1">
                                                    <Car className="w-4 h-4 text-gray-600" />
                                                    <span className="font-medium">
                                                        {getAutomobileDisplayName(request.automobile)}
                                                    </span>
                                                </div>
                                                {userType === 'agent' && request.customer && (
                                                    <p className="text-sm text-gray-600 mb-1">
                                                        Cliente: {getCustomerDisplayName(request.customer)}
                                                    </p>
                                                )}
                                                <div className="flex items-center gap-2 text-sm text-gray-600">
                                                    <Calendar className="w-3 h-3" />
                                                    <span>
                                                        {formatDate(request.desiredStartDate)} - {formatDate(request.desiredEndDate)}
                                                    </span>
                                                </div>
                                            </div>
                                            <div className="text-right">
                                                <div className={`inline-flex items-center gap-1 px-2 py-1 rounded-full text-xs font-medium ${getStatusColor(request.status || 'CREATED')}`}>
                                                    {getStatusIcon(request.status || 'CREATED')}
                                                    {getStatusLabel(request.status || 'CREATED')}
                                                </div>
                                                {request.estimatedValue && (
                                                    <p className="text-sm text-gray-600 mt-1">
                                                        {formatCurrency(safeNumber(request.estimatedValue))}
                                                    </p>
                                                )}
                                            </div>
                                        </div>
                                    </div>
                                ))}
                            </div>
                        )}
                    </div>

                    {/* Request Details */}
                    <div className="w-1/2 p-6 overflow-y-auto">
                        {selectedRequest ? (
                            <div className="space-y-6">
                                <div>
                                    <h3 className="text-xl font-bold mb-4">Detalhes da Solicitação</h3>

                                    {/* Status and Actions */}
                                    <div className="bg-gray-50 rounded-lg p-4 mb-6">
                                        <div className="flex items-center justify-between">
                                            <div className={`inline-flex items-center gap-2 px-3 py-2 rounded-full text-sm font-medium ${getStatusColor(selectedRequest.status || 'CREATED')}`}>
                                                {getStatusIcon(selectedRequest.status || 'CREATED')}
                                                {getStatusLabel(selectedRequest.status || 'CREATED')}
                                            </div>

                                            {userType === 'agent' && user?.userType !== 'cliente' && (
                                                <div className="flex gap-2">
                                                    {selectedRequest.status === RequestStatus.CREATED && (
                                                        <button
                                                            onClick={() => handleStatusChange(selectedRequest.id!, RequestStatus.UNDER_ANALYSIS)}
                                                            disabled={updatingStatus === selectedRequest.id}
                                                            className="px-3 py-1 bg-yellow-600 text-white rounded text-sm hover:bg-yellow-700 disabled:opacity-50"
                                                        >
                                                            Analisar
                                                        </button>
                                                    )}
                                                    {selectedRequest.status === RequestStatus.UNDER_ANALYSIS && (
                                                        <>
                                                            <button
                                                                onClick={() => handleStatusChange(selectedRequest.id!, RequestStatus.APPROVED)}
                                                                disabled={updatingStatus === selectedRequest.id}
                                                                className="px-3 py-1 bg-green-600 text-white rounded text-sm hover:bg-green-700 disabled:opacity-50"
                                                            >
                                                                Aprovar
                                                            </button>
                                                            <button
                                                                onClick={() => handleStatusChange(selectedRequest.id!, RequestStatus.REJECTED)}
                                                                disabled={updatingStatus === selectedRequest.id}
                                                                className="px-3 py-1 bg-red-600 text-white rounded text-sm hover:bg-red-700 disabled:opacity-50"
                                                            >
                                                                Rejeitar
                                                            </button>
                                                        </>
                                                    )}
                                                </div>
                                            )}
                                        </div>
                                    </div>

                                    {/* Vehicle Information */}
                                    {selectedRequest.automobile && (
                                        <div className="border rounded-lg p-4 mb-4">
                                            <h4 className="font-medium mb-2 flex items-center gap-2">
                                                <Car className="w-4 h-4" />
                                                Veículo
                                            </h4>
                                            <div className="text-sm text-gray-600 space-y-1">
                                                <p><strong>Modelo:</strong> {selectedRequest.automobile.brand} {selectedRequest.automobile.model}</p>
                                                <p><strong>Ano:</strong> {selectedRequest.automobile.year}</p>
                                                {selectedRequest.automobile && (
                                                    <p><strong>Valor Diário:</strong> {formatCurrency(safeNumber(selectedRequest.automobile.dailyRate))}</p>
                                                )}
                                            </div>
                                        </div>
                                    )}

                                    {/* Customer Information (for agents) */}
                                    {userType === 'agent' && selectedRequest.customer && (
                                        <div className="border rounded-lg p-4 mb-4">
                                            <h4 className="font-medium mb-2">Cliente</h4>
                                            <div className="text-sm text-gray-600 space-y-1">
                                                <p><strong>Nome:</strong> {selectedRequest.customer.name}</p>
                                                <p><strong>Email:</strong> {selectedRequest.customer.emailContact}</p>
                                            </div>
                                        </div>
                                    )}

                                    {/* Rental Details */}
                                    <div className="border rounded-lg p-4 mb-4">
                                        <h4 className="font-medium mb-2 flex items-center gap-2">
                                            <Calendar className="w-4 h-4" />
                                            Período do Aluguel
                                        </h4>
                                        <div className="text-sm text-gray-600 space-y-1">
                                            <p><strong>Início:</strong> {formatDate(selectedRequest.desiredStartDate)}</p>
                                            <p><strong>Término:</strong> {formatDate(selectedRequest.desiredEndDate)}</p>
                                            <p><strong>Duração:</strong> {
                                                calculateDaysBetween(selectedRequest.desiredStartDate, selectedRequest.desiredEndDate)
                                            } dias</p>
                                            {selectedRequest.estimatedValue && (
                                                <p><strong>Valor Estimado:</strong> {formatCurrency(safeNumber(selectedRequest.estimatedValue))}</p>
                                            )}
                                        </div>
                                    </div>

                                    {/* Additional Information */}
                                    <div className="border rounded-lg p-4">
                                        <h4 className="font-medium mb-2">Informações Adicionais</h4>
                                        <div className="text-sm text-gray-600 space-y-1">
                                            {selectedRequest.creationDate && (
                                                <p><strong>Data da Solicitação:</strong> {formatDate(selectedRequest.creationDate)}</p>
                                            )}
                                            {selectedRequest.observations && (
                                                <div>
                                                    <strong>Observações:</strong>
                                                    <p className="mt-1 bg-gray-50 p-2 rounded">{selectedRequest.observations}</p>
                                                </div>
                                            )}
                                        </div>
                                    </div>
                                </div>
                            </div>
                        ) : (
                            <div className="flex items-center justify-center h-full text-gray-500">
                                <div className="text-center">
                                    <Eye className="w-12 h-12 mx-auto mb-4 opacity-50" />
                                    <p>Selecione uma solicitação para ver os detalhes</p>
                                </div>
                            </div>
                        )}
                    </div>
                </div>
            </div>
        </div>
    )
}

export default RentalRequestsList