"use client"

import React, { useState, useEffect } from 'react'
import { ApiService } from '@/shared/services'
import type { CreditContractResponseDTO, CreditContractCreateDTO } from '@/shared/types/credit-contract'
import type { UserResponseDTO } from '@/shared/types/user'
import { CreditCard, Plus, Edit, Trash2, AlertCircle, TrendingUp, DollarSign, Users } from 'lucide-react'
import { formatCurrency } from '@/shared/utils/type-guards'

interface CreditManagementProps {
    className?: string
}

const CreditManagement: React.FC<CreditManagementProps> = ({ className = "" }) => {
    const [contracts, setContracts] = useState<CreditContractResponseDTO[]>([])
    const [customers, setCustomers] = useState<UserResponseDTO[]>([])
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState<string | null>(null)
    const [showForm, setShowForm] = useState(false)
    const [editingContract, setEditingContract] = useState<CreditContractResponseDTO | null>(null)
    const [formData, setFormData] = useState<CreditContractCreateDTO>({
        customerId: '',
        creditLimit: 0
    })

    useEffect(() => {
        loadData()
    }, [])

    const loadData = async () => {
        try {
            setLoading(true)
            setError(null)

            const [contractsData, usersData] = await Promise.all([
                ApiService.creditContract.getAllCreditContracts(),
                ApiService.auth.getCurrentUser()
                    .then(() => fetch(`${ApiService.auth['baseUrl']}/users`, {
                        headers: {
                            'Authorization': `Bearer ${ApiService.auth.getToken()}`
                        }
                    }))
                    .then(res => res.json())
                    .catch(() => [])
            ])

            setContracts(contractsData)

            // Filtrar apenas clientes
            const customersList = Array.isArray(usersData)
                ? usersData.filter((u: UserResponseDTO) => u.role === 'CUSTOMER')
                : []
            setCustomers(customersList)
        } catch (err) {
            console.error('Error loading data:', err)
            setError('Erro ao carregar dados. Verifique se você tem permissão de agente bancário.')
        } finally {
            setLoading(false)
        }
    }

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault()
        try {
            setError(null)

            if (!formData.customerId) {
                setError('Selecione um cliente')
                return
            }

            if (formData.creditLimit <= 0) {
                setError('Limite de crédito deve ser maior que zero')
                return
            }

            if (editingContract) {
                await ApiService.creditContract.updateCreditContract(editingContract.id, {
                    creditLimit: formData.creditLimit
                })
            } else {
                await ApiService.creditContract.createOrUpdateCreditLimit(formData)
            }

            await loadData()
            resetForm()
        } catch (err: any) {
            setError(err.message || 'Erro ao salvar contrato de crédito.')
            console.error('Error saving contract:', err)
        }
    }

    const handleEdit = (contract: CreditContractResponseDTO) => {
        setEditingContract(contract)
        setFormData({
            customerId: contract.customer.id,
            creditLimit: contract.creditLimit
        })
        setShowForm(true)
    }

    const handleDelete = async (id: string) => {
        if (!confirm('Tem certeza que deseja excluir este contrato de crédito?')) {
            return
        }

        try {
            setError(null)
            await ApiService.creditContract.deleteCreditContract(id)
            await loadData()
        } catch (err) {
            setError('Erro ao excluir contrato.')
            console.error('Error deleting contract:', err)
        }
    }

    const resetForm = () => {
        setFormData({
            customerId: '',
            creditLimit: 0
        })
        setEditingContract(null)
        setShowForm(false)
    }

    const formatDate = (dateString: string) => {
        return new Date(dateString).toLocaleDateString('pt-BR')
    }

    const getUsageColor = (percentage: number) => {
        if (percentage >= 90) return 'text-red-600 bg-red-100'
        if (percentage >= 70) return 'text-yellow-600 bg-yellow-100'
        return 'text-green-600 bg-green-100'
    }

    if (loading) {
        return (
            <div className={`${className} flex items-center justify-center py-12`}>
                <div className="text-white/80 text-lg">Carregando contratos...</div>
            </div>
        )
    }

    return (
        <div className={`${className} bg-white/10 backdrop-blur-md rounded-xl border border-white/20 p-6`}>
            {/* Header */}
            <div className="flex justify-between items-center mb-6">
                <h2 className="text-2xl font-bold text-white flex items-center gap-2">
                    <CreditCard className="w-6 h-6" />
                    Contratos de Crédito
                </h2>
                <button
                    onClick={() => setShowForm(true)}
                    className="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg flex items-center gap-2 transition-colors"
                >
                    <Plus className="w-4 h-4" />
                    Novo Contrato
                </button>
            </div>

            {error && (
                <div className="bg-red-500/20 border border-red-500/50 rounded-lg p-4 mb-6 flex items-center gap-2">
                    <AlertCircle className="w-5 h-5 text-red-400" />
                    <span className="text-red-200">{error}</span>
                </div>
            )}

            {/* Statistics Cards */}
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-6">
                <div className="bg-white/5 backdrop-blur-sm rounded-lg p-4 border border-white/10">
                    <div className="flex items-center justify-between">
                        <div>
                            <p className="text-white/70 text-sm">Total de Contratos</p>
                            <p className="text-2xl font-bold text-white">{contracts.length}</p>
                        </div>
                        <Users className="w-8 h-8 text-blue-400" />
                    </div>
                </div>

                <div className="bg-white/5 backdrop-blur-sm rounded-lg p-4 border border-white/10">
                    <div className="flex items-center justify-between">
                        <div>
                            <p className="text-white/70 text-sm">Limite Total Concedido</p>
                            <p className="text-2xl font-bold text-white">
                                {formatCurrency(contracts.reduce((sum, c) => sum + c.creditLimit, 0))}
                            </p>
                        </div>
                        <DollarSign className="w-8 h-8 text-green-400" />
                    </div>
                </div>

                <div className="bg-white/5 backdrop-blur-sm rounded-lg p-4 border border-white/10">
                    <div className="flex items-center justify-between">
                        <div>
                            <p className="text-white/70 text-sm">Em Uso</p>
                            <p className="text-2xl font-bold text-white">
                                {formatCurrency(contracts.reduce((sum, c) => sum + c.usedLimit, 0))}
                            </p>
                        </div>
                        <TrendingUp className="w-8 h-8 text-yellow-400" />
                    </div>
                </div>
            </div>

            {/* Form Modal */}
            {showForm && (
                <div className="bg-white/5 backdrop-blur-sm rounded-lg p-6 mb-6 border border-white/10">
                    <h3 className="text-xl font-semibold text-white mb-4">
                        {editingContract ? 'Editar Contrato' : 'Novo Contrato'}
                    </h3>

                    <form onSubmit={handleSubmit} className="space-y-4">
                        <div>
                            <label className="block text-white/80 text-sm mb-2">
                                Cliente *
                            </label>
                            <select
                                value={formData.customerId}
                                onChange={(e) => setFormData({...formData, customerId: e.target.value})}
                                className="w-full px-3 py-2 bg-white/10 border border-white/20 rounded-lg text-white focus:outline-none focus:ring-2 focus:ring-blue-500"
                                required
                                disabled={!!editingContract}
                            >
                                <option value="">Selecione um cliente</option>
                                {customers.map((customer) => (
                                    <option key={customer.id} value={customer.id} className="bg-gray-800">
                                        {customer.username} - {customer.email}
                                    </option>
                                ))}
                            </select>
                        </div>

                        <div>
                            <label className="block text-white/80 text-sm mb-2">
                                Limite de Crédito (R$) *
                            </label>
                            <input
                                type="number"
                                step="0.01"
                                min="0"
                                value={formData.creditLimit}
                                onChange={(e) => setFormData({...formData, creditLimit: parseFloat(e.target.value)})}
                                className="w-full px-3 py-2 bg-white/10 border border-white/20 rounded-lg text-white placeholder-white/50 focus:outline-none focus:ring-2 focus:ring-blue-500"
                                placeholder="1000.00"
                                required
                            />
                        </div>

                        <div className="flex gap-4 pt-4">
                            <button
                                type="submit"
                                className="bg-green-600 hover:bg-green-700 text-white px-6 py-2 rounded-lg transition-colors"
                            >
                                {editingContract ? 'Atualizar' : 'Criar'} Contrato
                            </button>
                            <button
                                type="button"
                                onClick={resetForm}
                                className="bg-gray-600 hover:bg-gray-700 text-white px-6 py-2 rounded-lg transition-colors"
                            >
                                Cancelar
                            </button>
                        </div>
                    </form>
                </div>
            )}

            {/* Contracts List */}
            <div className="space-y-4">
                {contracts.length === 0 ? (
                    <div className="text-center py-8 text-white/60">
                        Nenhum contrato de crédito cadastrado ainda.
                    </div>
                ) : (
                    contracts.map((contract) => (
                        <div
                            key={contract.id}
                            className="bg-white/5 backdrop-blur-sm rounded-lg p-4 border border-white/10"
                        >
                            <div className="flex justify-between items-start">
                                <div className="flex-1">
                                    <div className="flex items-center gap-3 mb-3">
                                        <CreditCard className="w-5 h-5 text-blue-400" />
                                        <h3 className="text-lg font-semibold text-white">
                                            {contract.customer.name || contract.customer.emailContact}
                                        </h3>
                                        <span className={`px-2 py-1 rounded-full text-xs font-medium ${
                                            contract.status === 'ACTIVE'
                                                ? 'bg-green-500/20 text-green-400 border border-green-500/30'
                                                : 'bg-gray-500/20 text-gray-400 border border-gray-500/30'
                                        }`}>
                                            {contract.status === 'ACTIVE' ? 'Ativo' : contract.status}
                                        </span>
                                    </div>

                                    <div className="grid grid-cols-2 md:grid-cols-4 gap-4 text-sm text-white/80">
                                        <div>
                                            <span className="font-medium">Limite Total:</span>
                                            <p className="text-green-400 font-semibold">
                                                {formatCurrency(contract.creditLimit)}
                                            </p>
                                        </div>
                                        <div>
                                            <span className="font-medium">Disponível:</span>
                                            <p className="text-blue-400 font-semibold">
                                                {formatCurrency(contract.availableLimit)}
                                            </p>
                                        </div>
                                        <div>
                                            <span className="font-medium">Em Uso:</span>
                                            <p className="text-yellow-400 font-semibold">
                                                {formatCurrency(contract.usedLimit)}
                                            </p>
                                        </div>
                                        <div>
                                            <span className="font-medium">Uso:</span>
                                            <p className={`font-semibold px-2 py-1 rounded ${getUsageColor(contract.usagePercentage)}`}>
                                                {contract.usagePercentage.toFixed(1)}%
                                            </p>
                                        </div>
                                    </div>

                                    <div className="mt-3">
                                        <div className="w-full bg-white/10 rounded-full h-2">
                                            <div
                                                className={`h-2 rounded-full transition-all ${
                                                    contract.usagePercentage >= 90 ? 'bg-red-500' :
                                                        contract.usagePercentage >= 70 ? 'bg-yellow-500' :
                                                            'bg-green-500'
                                                }`}
                                                style={{ width: `${Math.min(contract.usagePercentage, 100)}%` }}
                                            />
                                        </div>
                                    </div>

                                    <div className="mt-3 text-xs text-white/60">
                                        <span>Criado em: {formatDate(contract.createdAt)}</span>
                                        {' • '}
                                        <span>Atualizado em: {formatDate(contract.updatedAt)}</span>
                                    </div>
                                </div>

                                <div className="flex gap-2 ml-4">
                                    <button
                                        onClick={() => handleEdit(contract)}
                                        className="bg-blue-600 hover:bg-blue-700 text-white p-2 rounded-lg transition-colors"
                                        title="Editar"
                                    >
                                        <Edit className="w-4 h-4" />
                                    </button>
                                    <button
                                        onClick={() => handleDelete(contract.id)}
                                        className="bg-red-600 hover:bg-red-700 text-white p-2 rounded-lg transition-colors"
                                        title="Excluir"
                                    >
                                        <Trash2 className="w-4 h-4" />
                                    </button>
                                </div>
                            </div>
                        </div>
                    ))
                )}
            </div>
        </div>
    )
}

export default CreditManagement