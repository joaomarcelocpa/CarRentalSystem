// app/components/CustomerProfileForm.tsx
"use client"

import type React from "react"
import { useState, useEffect } from "react"
import { useAuth } from "../contexts/AuthContext"
import { ApiService } from "../services"
import type { CustomerCreateDTO, CustomerResponseDTO } from "../types/customer"
import { User, Mail, MapPin, Briefcase, FileText, X, Save } from "lucide-react"
import { safeString } from "../utils/type-guards"

interface CustomerProfileFormProps {
    onClose: () => void
    onSuccess?: (customer: CustomerResponseDTO) => void
}

const CustomerProfileForm: React.FC<CustomerProfileFormProps> = ({ onClose, onSuccess }) => {
    const { user } = useAuth()
    const [formData, setFormData] = useState<CustomerCreateDTO>({
        name: "",
        emailContact: "",
        rg: "",
        cpf: "",
        address: "",
        profession: ""
    })
    const [loading, setLoading] = useState(true)
    const [saving, setSaving] = useState(false)
    const [error, setError] = useState("")

    useEffect(() => {
        const fetchCustomerData = async () => {
            if (!user || user.userType !== 'cliente') {
                setLoading(false)
                return
            }

            try {
                const customerData = await ApiService.customer.getCustomerById(user.id)
                if (customerData) {
                    setFormData({
                        name: safeString(customerData.name, ""),
                        emailContact: safeString(customerData.emailContact, ""),
                        rg: safeString(customerData.rg, ""),
                        cpf: safeString(customerData.cpf, ""),
                        address: safeString(customerData.address, ""),
                        profession: safeString(customerData.profession, "")
                    })
                }
            } catch (error) {
                console.error("Error fetching customer data:", error)
                setError("Erro ao carregar dados do perfil")
            } finally {
                setLoading(false)
            }
        }

        fetchCustomerData()
    }, [user])

    const handleInputChange = (field: keyof CustomerCreateDTO, value: string) => {
        setFormData(prev => ({
            ...prev,
            [field]: value
        }))
    }

    const validateForm = (): boolean => {
        if (!formData.name.trim()) {
            setError("Nome é obrigatório")
            return false
        }

        if (!formData.emailContact.trim()) {
            setError("Email é obrigatório")
            return false
        }

        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
        if (!emailRegex.test(formData.emailContact)) {
            setError("Email inválido")
            return false
        }

        // Validate CPF format (basic validation)
        if (formData.cpf && formData.cpf.length > 0 && formData.cpf.replace(/\D/g, '').length !== 11) {
            setError("CPF deve ter 11 dígitos")
            return false
        }

        return true
    }

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault()

        if (!user || !validateForm()) {
            return
        }

        setSaving(true)
        setError("")

        try {
            const updatedCustomer = await ApiService.customer.updateCustomer(user.id, formData)

            if (updatedCustomer && onSuccess) {
                onSuccess(updatedCustomer)
            }

            onClose()
        } catch (error) {
            console.error("Error updating profile:", error)
            setError("Erro ao atualizar perfil. Tente novamente.")
        } finally {
            setSaving(false)
        }
    }

    const formatCPF = (value: string) => {
        const numbers = value.replace(/\D/g, '')
        return numbers.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, '$1.$2.$3-$4')
    }

    const handleCPFChange = (value: string) => {
        const formatted = formatCPF(value)
        if (formatted.length <= 14) {
            handleInputChange('cpf', formatted)
        }
    }

    if (loading) {
        return (
            <div className="fixed inset-0 bg-black/50 flex items-center justify-center p-4 z-50">
                <div className="bg-white rounded-2xl shadow-2xl w-full max-w-2xl h-96 flex items-center justify-center">
                    <div className="text-xl">Carregando dados do perfil...</div>
                </div>
            </div>
        )
    }

    return (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center p-4 z-50">
            <div className="bg-white rounded-2xl shadow-2xl w-full max-w-2xl max-h-[90vh] overflow-y-auto">
                <div className="p-6">
                    {/* Header */}
                    <div className="flex justify-between items-center mb-6">
                        <div className="flex items-center gap-3">
                            <div className="w-10 h-10 bg-blue-600 rounded-full flex items-center justify-center">
                                <User className="w-5 h-5 text-white" />
                            </div>
                            <div>
                                <h2 className="text-2xl font-bold text-gray-900">Editar Perfil</h2>
                                <p className="text-gray-600 text-sm">Atualize suas informações pessoais</p>
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
                        {/* Basic Information */}
                        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-2">
                                    <User className="w-4 h-4 inline mr-2" />
                                    Nome Completo *
                                </label>
                                <input
                                    type="text"
                                    value={formData.name}
                                    onChange={(e) => handleInputChange('name', e.target.value)}
                                    className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                                    placeholder="Digite seu nome completo"
                                    required
                                />
                            </div>

                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-2">
                                    <Mail className="w-4 h-4 inline mr-2" />
                                    Email *
                                </label>
                                <input
                                    type="email"
                                    value={formData.emailContact}
                                    onChange={(e) => handleInputChange('emailContact', e.target.value)}
                                    className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                                    placeholder="seu@email.com"
                                    required
                                />
                            </div>
                        </div>

                        {/* Documents */}
                        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-2">
                                    <FileText className="w-4 h-4 inline mr-2" />
                                    RG
                                </label>
                                <input
                                    type="text"
                                    value={formData.rg}
                                    onChange={(e) => handleInputChange('rg', e.target.value)}
                                    className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                                    placeholder="00.000.000-0"
                                />
                            </div>

                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-2">
                                    <FileText className="w-4 h-4 inline mr-2" />
                                    CPF
                                </label>
                                <input
                                    type="text"
                                    value={formData.cpf}
                                    onChange={(e) => handleCPFChange(e.target.value)}
                                    className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                                    placeholder="000.000.000-00"
                                />
                            </div>
                        </div>

                        {/* Address */}
                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-2">
                                <MapPin className="w-4 h-4 inline mr-2" />
                                Endereço
                            </label>
                            <textarea
                                value={formData.address}
                                onChange={(e) => handleInputChange('address', e.target.value)}
                                rows={3}
                                className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                                placeholder="Digite seu endereço completo"
                            />
                        </div>

                        {/* Profession */}
                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-2">
                                <Briefcase className="w-4 h-4 inline mr-2" />
                                Profissão
                            </label>
                            <input
                                type="text"
                                value={formData.profession}
                                onChange={(e) => handleInputChange('profession', e.target.value)}
                                className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                                placeholder="Digite sua profissão"
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
                                disabled={saving}
                                className="flex-1 px-6 py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
                            >
                                {saving ? (
                                    <div className="flex items-center justify-center gap-2">
                                        <div className="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin" />
                                        Salvando...
                                    </div>
                                ) : (
                                    <div className="flex items-center justify-center gap-2">
                                        <Save className="w-4 h-4" />
                                        Salvar Alterações
                                    </div>
                                )}
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    )
}

export default CustomerProfileForm