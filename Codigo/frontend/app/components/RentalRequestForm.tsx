"use client"

import type React from "react"
import { useState, useEffect } from "react"
import { useAuth } from "../contexts/AuthContext"
import { ApiService } from "../services"
import type { Automobile } from "../types/automobile"
import type { RentalRequest } from "../types/rental-request"
import { Calendar, Car, Clock, DollarSign, X } from "lucide-react"

interface RentalRequestFormProps {
    onClose: () => void
    onSuccess?: (request: RentalRequest) => void
}

const RentalRequestForm: React.FC<RentalRequestFormProps> = ({ onClose, onSuccess }) => {
    const { user } = useAuth()
    const [automobiles, setAutomobiles] = useState<Automobile[]>([])
    const [selectedAutomobile, setSelectedAutomobile] = useState<string>("")
    const [startDate, setStartDate] = useState("")
    const [endDate, setEndDate] = useState("")
    const [observations, setObservations] = useState("")
    const [isLoading, setIsLoading] = useState(false)
    const [error, setError] = useState("")
    const [estimatedValue, setEstimatedValue] = useState<number>(0)

    useEffect(() => {
        const fetchAutomobiles = async () => {
            try {
                const availableCars = await ApiService.automobile.getAvailableAutomobiles()
                setAutomobiles(availableCars)
            } catch (error) {
                console.error("Error fetching automobiles:", error)
                setError("Erro ao carregar veículos disponíveis")
            }
        }

        fetchAutomobiles()
    }, [])

    useEffect(() => {
        if (selectedAutomobile && startDate && endDate) {
            const car = automobiles.find(a => a.id === selectedAutomobile)
            if (car) {
                const start = new Date(startDate)
                const end = new Date(endDate)
                const diffTime = Math.abs(end.getTime() - start.getTime())
                const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24))
                setEstimatedValue(diffDays * car.dailyRate)
            }
        } else {
            setEstimatedValue(0)
        }
    }, [selectedAutomobile, startDate, endDate, automobiles])

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault()

        if (!user) {
            setError("Usuário não autenticado")
            return
        }

        if (!selectedAutomobile || !startDate || !endDate) {
            setError("Todos os campos obrigatórios devem ser preenchidos")
            return
        }

        const start = new Date(startDate)
        const end = new Date(endDate)

        if (end <= start) {
            setError("A data final deve ser posterior à data inicial")
            return
        }

        if (start < new Date()) {
            setError("A data inicial não pode ser anterior a hoje")
            return
        }

        setIsLoading(true)
        setError("")

        try {
            const requestData: RentalRequest = {
                desiredStartDate: startDate,
                desiredEndDate: endDate,
                observations,
                customer: {
                    id: user.id,
                    name: user.name,
                    emailContact: user.email
                },
                automobile: {
                    id: selectedAutomobile
                }
            }

            const createdRequest = await ApiService.rentalRequest.createRentalRequest(requestData)

            if (onSuccess) {
                onSuccess(createdRequest)
            }

            onClose()
        } catch (error) {
            console.error("Error creating rental request:", error)
            setError("Erro ao criar solicitação de aluguel. Tente novamente.")
        } finally {
            setIsLoading(false)
        }
    }

    const formatCurrency = (value: number) => {
        return new Intl.NumberFormat('pt-BR', {
            style: 'currency',
            currency: 'BRL'
        }).format(value)
    }

    return (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center p-4 z-50">
            <div className="bg-white rounded-2xl shadow-2xl w-full max-w-2xl max-h-[90vh] overflow-y-auto">
                <div className="p-6">
                    {/* Header */}
                    <div className="flex justify-between items-center mb-6">
                        <div className="flex items-center gap-3">
                            <div className="w-10 h-10 bg-blue-600 rounded-full flex items-center justify-center">
                                <Car className="w-5 h-5 text-white" />
                            </div>
                            <div>
                                <h2 className="text-2xl font-bold text-gray-900">Solicitar Aluguel</h2>
                                <p className="text-gray-600 text-sm">Preencha os dados para sua solicitação</p>
                            </div>
                        </div>
                        <button
                            onClick={onClose}
                            className="text-gray-400 hover:text-gray-600 transition-colors"
                        >
                            <X className="w-6 h-6" />
                        </button>
                    </div>

                    {error && (
                        <div className="mb-6 p-4 bg-red-50 border border-red-200 rounded-lg">
                            <p className="text-red-600 text-sm">{error}</p>
                        </div>
                    )}

                    <form onSubmit={handleSubmit} className="space-y-6">
                        {/* Vehicle Selection */}
                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-2">
                                <Car className="w-4 h-4 inline mr-2" />
                                Selecionar Veículo *
                            </label>
                            <select
                                value={selectedAutomobile}
                                onChange={(e) => setSelectedAutomobile(e.target.value)}
                                className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                                required
                            >
                                <option value="">Selecione um veículo</option>
                                {automobiles.map((car) => (
                                    <option key={car.id} value={car.id}>
                                        {car.brand} {car.model} ({car.year}) - {formatCurrency(car.dailyRate)}/dia
                                    </option>
                                ))}
                            </select>
                        </div>

                        {/* Date Selection */}
                        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-2">
                                    <Calendar className="w-4 h-4 inline mr-2" />
                                    Data de Início *
                                </label>
                                <input
                                    type="date"
                                    value={startDate}
                                    onChange={(e) => setStartDate(e.target.value)}
                                    min={new Date().toISOString().split('T')[0]}
                                    className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                                    required
                                />
                            </div>
                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-2">
                                    <Calendar className="w-4 h-4 inline mr-2" />
                                    Data de Término *
                                </label>
                                <input
                                    type="date"
                                    value={endDate}
                                    onChange={(e) => setEndDate(e.target.value)}
                                    min={startDate || new Date().toISOString().split('T')[0]}
                                    className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                                    required
                                />
                            </div>
                        </div>

                        {/* Estimated Value Display */}
                        {estimatedValue > 0 && (
                            <div className="bg-blue-50 border border-blue-200 rounded-lg p-4">
                                <div className="flex items-center gap-2">
                                    <DollarSign className="w-5 h-5 text-blue-600" />
                                    <span className="font-medium text-blue-800">
                                        Valor Estimado: {formatCurrency(estimatedValue)}
                                    </span>
                                </div>
                                {startDate && endDate && (
                                    <p className="text-sm text-blue-600 mt-1">
                                        {Math.ceil(Math.abs(new Date(endDate).getTime() - new Date(startDate).getTime()) / (1000 * 60 * 60 * 24))} dias
                                    </p>
                                )}
                            </div>
                        )}

                        {/* Observations */}
                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-2">
                                Observações
                            </label>
                            <textarea
                                value={observations}
                                onChange={(e) => setObservations(e.target.value)}
                                rows={4}
                                className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                                placeholder="Adicione observações ou solicitações especiais..."
                            />
                        </div>

                        {/* Action Buttons */}
                        <div className="flex gap-4 pt-4">
                            <button
                                type="button"
                                onClick={onClose}
                                className="flex-1 px-6 py-3 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50 transition-colors"
                            >
                                Cancelar
                            </button>
                            <button
                                type="submit"
                                disabled={isLoading || !selectedAutomobile || !startDate || !endDate}
                                className="flex-1 px-6 py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
                            >
                                {isLoading ? (
                                    <div className="flex items-center justify-center gap-2">
                                        <div className="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin" />
                                        Enviando...
                                    </div>
                                ) : (
                                    "Enviar Solicitação"
                                )}
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    )
}

export default RentalRequestForm