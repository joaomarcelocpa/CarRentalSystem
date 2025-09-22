"use client"

import type React from "react"
import { useState, useEffect } from "react"
import { useAuth } from "@/shared/contexts/AuthContext"
import { ApiService } from "../shared/services"
import type { Automobile } from "@/shared/types/automobile"
import type { RentalRequest } from "@/shared/types/rental-request"
import {
    Calendar,
    X,
    CheckCircle,
    Star,
    Fuel,
    Users,
    Cog,
    MapPin,
    Shield,
    Phone,
    Mail
} from "lucide-react"
import { formatCurrency, safeNumber } from "@/shared/utils/type-guards"

interface RentalConfirmationModalProps {
    selectedCar: Automobile
    onClose: () => void
    onSuccess?: (request: RentalRequest) => void
}

const RentalConfirmationModal: React.FC<RentalConfirmationModalProps> = ({
                                                                             selectedCar,
                                                                             onClose,
                                                                             onSuccess
                                                                         }) => {
    const { user } = useAuth()
    const [startDate, setStartDate] = useState("")
    const [endDate, setEndDate] = useState("")
    const [observations, setObservations] = useState("")
    const [isLoading, setIsLoading] = useState(false)
    const [error, setError] = useState("")
    const [estimatedValue, setEstimatedValue] = useState<number>(0)
    const [pickupLocation, setPickupLocation] = useState("Agência Central - Centro")
    const [returnLocation, setReturnLocation] = useState("Agência Central - Centro")

    useEffect(() => {
        if (startDate && endDate) {
            const start = new Date(startDate)
            const end = new Date(endDate)
            const diffTime = Math.abs(end.getTime() - start.getTime())
            const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24))
            setEstimatedValue(diffDays * safeNumber(selectedCar.dailyRate))
        } else {
            setEstimatedValue(0)
        }
    }, [startDate, endDate, selectedCar.dailyRate])

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault()

        if (!user) {
            setError("Usuário não autenticado")
            return
        }

        if (!startDate || !endDate) {
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
                observations: observations +
                    `\nLocal de retirada: ${pickupLocation}` +
                    `\nLocal de devolução: ${returnLocation}`,
                customer: {
                    id: user.id,
                    name: user.name,
                    emailContact: user.email
                },
                automobile: {
                    id: selectedCar.id!
                }
            }

            const createdRequest = await ApiService.rentalRequest.createRentalRequest(requestData)

            if (onSuccess) {
                onSuccess(createdRequest)
            }
        } catch (error) {
            console.error("Error creating rental request:", error)
            setError("Erro ao criar solicitação de aluguel. Tente novamente.")
        } finally {
            setIsLoading(false)
        }
    }

    const getImageUrl = (car: Automobile) => {
        const imageName = `${car.brand.toLowerCase()}-${car.model.toLowerCase().replace(/\s+/g, '-')}.jpeg`
        return `/${imageName}`
    }

    const getCarCategory = (car: Automobile) => {
        const rate = safeNumber(car.dailyRate)
        if (rate <= 100) return "Econômico"
        if (rate <= 150) return "Padrão"
        if (rate <= 200) return "Premium"
        return "Luxo"
    }

    const getDaysCount = () => {
        if (!startDate || !endDate) return 0
        const start = new Date(startDate)
        const end = new Date(endDate)
        const diffTime = Math.abs(end.getTime() - start.getTime())
        return Math.ceil(diffTime / (1000 * 60 * 60 * 24))
    }

    const locations = [
        "Agência Central - Centro",
        "Agência Aeroporto Internacional",
        "Agência Shopping Center",
        "Agência Rodoviária",
        "Agência Zona Sul"
    ]

    return (
        <div className="fixed inset-0 bg-black/80 bg-opacity-60 flex items-center justify-center p-2 z-30">
            <div className="bg-white rounded-2xl shadow-2xl w-full max-w-4xl">
                {/* Header */}
                <div className="bg-gradient-to-r from-blue-600 to-blue-700 text-white p-3">
                    <div className="flex justify-between items-center">
                        <div>
                            <h2 className="text-xl font-bold ml-2">Confirmar Aluguel - {selectedCar.brand} {selectedCar.model} ({selectedCar.year})</h2>
                        </div>
                        <button
                            onClick={onClose}
                            className="text-white hover:text-red-600 hover:bg-opacity-10 p-2 rounded-full transition-colors"
                        >
                            <X className="w-6 h-6" />
                        </button>
                    </div>
                </div>

                <div className="flex flex-col lg:flex-row h-full max-h-96 lg:max-h-screen overflow-hidden">
                    {/* Car Info - Left Side */}
                    <div className="lg:w-2-5 p-6 border-r bg-gray-50 overflow-y-auto" style={{width: '40%'}}>
                        {/* Car Image */}
                        <div className="relative h-40 bg-gradient-to-br from-gray-100 to-gray-200 rounded-xl overflow-hidden mb-4">
                            <img
                                src={getImageUrl(selectedCar)}
                                alt={`${selectedCar.brand} ${selectedCar.model}`}
                                className="w-full h-full object-cover"
                                onError={(e) => {
                                    const target = e.target as HTMLImageElement;
                                    target.src = "data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iNDAwIiBoZWlnaHQ9IjMwMCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj4KICA8cmVjdCB3aWR0aD0iMTAwJSIgaGVpZ2h0PSIxMDAlIiBmaWxsPSIjZjNmNGY2Ii8+CiAgPHRleHQgeD0iNTAlIiB5PSI0NSUiIGZvbnQtZmFtaWx5PSJBcmlhbCwgc2Fucy1zZXJpZiIgZm9udC1zaXplPSIyNCIgZmlsbD0iIzk0YTNiOCIgdGV4dC1hbmNob3I9Im1pZGRsZSIgZHk9Ii4zZW0iPkNhcnJvPC90ZXh0PgogIDx0ZXh0IHg9IjUwJSIgeT0iNTUlIiBmb250LWZhbWlseT0iQXJpYWwsIHNhbnMtc2VyaWYiIGZvbnQtc2l6ZT0iMTgiIGZpbGw9IiM5NDk0YWMiIHRleHQtYW5jaG9yPSJtaWRkbGUiIGR5PSIuM2VtIj5JbWFnZW0gSW5kaXNwb27DrXZlbDwvdGV4dD4KPC9zdmc+";
                                }}
                            />
                            <div className="absolute top-3 left-3 bg-blue-600 text-white px-2 py-1 rounded-full text-xs font-medium">
                                {getCarCategory(selectedCar)}
                            </div>
                        </div>

                        {/* Features Grid */}
                        <div className="grid grid-cols-2 gap-3 mb-4">
                            <div className="bg-white p-3 rounded-lg border text-center">
                                <Users className="w-5 h-5 text-blue-600 mx-auto mb-1" />
                                <span className="text-sm font-medium text-black">5 pessoas</span>
                            </div>
                            <div className="bg-white p-3 rounded-lg border text-center">
                                <Cog className="w-5 h-5 text-blue-600 mx-auto mb-1" />
                                <span className="text-sm font-medium text-black">Automático</span>
                            </div>
                            <div className="bg-white p-3 rounded-lg border text-center">
                                <Fuel className="w-5 h-5 text-blue-600 mx-auto mb-1" />
                                <span className="text-sm font-medium text-black">Flex</span>
                            </div>
                            <div className="bg-white p-3 rounded-lg border text-center">
                                <Shield className="w-5 h-5 text-blue-600 mx-auto mb-1" />
                                <span className="text-sm font-medium text-black">Seguro</span>
                            </div>
                        </div>

                        {/* Price per day */}
                        <div className="text-center mb-4">
                            <div className="text-2xl font-bold text-blue-600">
                                {formatCurrency(safeNumber(selectedCar.dailyRate))}
                            </div>
                            <div className="text-sm text-gray-500">por dia</div>
                        </div>
                    </div>

                    {/* Form - Right Side */}
                    <div className="lg:w-3-5 p-6 overflow-y-auto" style={{width: '60%'}}>
                        {error && (
                            <div className="mb-4 p-3 bg-red-50 border border-red-200 rounded-lg">
                                <p className="text-red-600 text-sm">{error}</p>
                            </div>
                        )}

                        <form onSubmit={handleSubmit} className="space-y-4">
                            {/* Dates */}
                            <div className="grid grid-cols-2 gap-3">
                                <div>
                                    <label className="block text-sm font-medium text-gray-700 mb-1">
                                        <Calendar className="w-4 h-4 inline mr-1" />
                                        Devolução *
                                    </label>
                                    <input
                                        type="date"
                                        value={endDate}
                                        onChange={(e) => setEndDate(e.target.value)}
                                        min={startDate || new Date().toISOString().split('T')[0]}
                                        className="w-full px-3 py-2 text-black border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent text-sm"
                                        required
                                    />
                                </div>
                            </div>

                            {/* Locations */}
                            <div className="grid grid-cols-2 gap-3">
                                <div>
                                    <label className="block text-sm font-medium text-gray-700 mb-1">
                                        <MapPin className="w-4 h-4 inline mr-1" />
                                        Local de Retirada
                                    </label>
                                    <select
                                        value={pickupLocation}
                                        onChange={(e) => setPickupLocation(e.target.value)}
                                        className="w-full px-3 py-2 border text-black border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent text-sm"
                                    >
                                        {locations.map((location) => (
                                            <option key={location} value={location}>
                                                {location}
                                            </option>
                                        ))}
                                    </select>
                                </div>
                                <div>
                                    <label className="block text-sm font-medium text-gray-700 mb-1">
                                        <MapPin className="w-4 h-4 inline mr-1" />
                                        Local de Devolução
                                    </label>
                                    <select
                                        value={returnLocation}
                                        onChange={(e) => setReturnLocation(e.target.value)}
                                        className="w-full px-3 py-2 border text-black border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent text-sm"
                                    >
                                        {locations.map((location) => (
                                            <option key={location} value={location}>
                                                {location}
                                            </option>
                                        ))}
                                    </select>
                                </div>
                            </div>

                            {/* Price Summary */}
                            {estimatedValue > 0 && (
                                <div className="bg-blue-50 border border-blue-200 rounded-lg p-4">
                                    <h4 className="text-lg font-semibold text-blue-800 mb-3">Resumo do Pedido</h4>
                                    <div className="space-y-2">
                                        <div className="flex justify-between text-sm">
                                            <span className="text-blue-700">Diária do veículo</span>
                                            <span className="font-medium">{formatCurrency(safeNumber(selectedCar.dailyRate))}</span>
                                        </div>
                                        <div className="flex justify-between text-sm">
                                            <span className="text-blue-700">Quantidade de dias</span>
                                            <span className="font-medium">{getDaysCount()} {getDaysCount() === 1 ? 'dia' : 'dias'}</span>
                                        </div>
                                        <div className="border-t border-blue-200 pt-2">
                                            <div className="flex justify-between items-center">
                                                <span className="text-base font-semibold text-blue-800">Total Estimado</span>
                                                <span className="text-xl font-bold text-blue-800">{formatCurrency(estimatedValue)}</span>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            )}

                            {/* Observations */}
                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-1">
                                    Observações Especiais
                                </label>
                                <textarea
                                    value={observations}
                                    onChange={(e) => setObservations(e.target.value)}
                                    rows={3}
                                    className="w-full px-3 py-2 text-black border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent text-sm"
                                    placeholder="Adicione observações ou solicitações especiais..."
                                />
                            </div>

                            {/* Contact Info */}
                            <div className="bg-gray-50 border border-gray-200 rounded-lg p-3">
                                <h4 className="text-sm font-semibold text-gray-800 mb-2">Informações de Contato</h4>
                                <div className="grid grid-cols-2 gap-3 text-xs">
                                    <div className="flex items-center gap-2 text-gray-700">
                                        <Mail className="w-4 h-4 text-blue-600" />
                                        <div>
                                            <p className="font-medium">Email</p>
                                            <p>{user?.email}</p>
                                        </div>
                                    </div>
                                    <div className="flex items-center gap-2 text-gray-700">
                                        <Phone className="w-4 h-4 text-blue-600" />
                                        <div>
                                            <p className="font-medium">Suporte 24h</p>
                                            <p>(11) 9999-9999</p>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            {/* Action Buttons */}
                            <div className="flex gap-3 pt-4">
                                <button
                                    type="button"
                                    onClick={onClose}
                                    className="flex-1 px-4 py-2 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50 transition-colors text-sm font-medium"
                                >
                                    Cancelar
                                </button>
                                <button
                                    type="submit"
                                    disabled={isLoading || !startDate || !endDate}
                                    className="flex-1 px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors text-sm font-medium"
                                >
                                    {isLoading ? (
                                        <div className="flex items-center justify-center gap-2">
                                            <div className="w-4 h-4 border-2 border-white border-opacity-30 border-t-white rounded-full animate-spin" />
                                            Processando...
                                        </div>
                                    ) : (
                                        <div className="flex items-center justify-center gap-2">
                                            <CheckCircle className="w-4 h-4" />
                                            Confirmar Aluguel
                                        </div>
                                    )}
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    )
}
export default RentalConfirmationModal