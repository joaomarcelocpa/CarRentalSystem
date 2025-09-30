/* eslint-disable react-hooks/exhaustive-deps */
/* eslint-disable @typescript-eslint/no-explicit-any */
"use client"

import React, { useState, useEffect } from 'react'
import { AutomobileService } from '@/shared/services/automobile.service'
import type { AutomobileResponse, AutomobileCreate } from '@/shared/types/automobile'
import { Plus, Edit, Trash2, Car, AlertCircle } from 'lucide-react'

interface AutomobileManagementProps {
    className?: string
}

const AutomobileManagement: React.FC<AutomobileManagementProps> = ({ className = "" }) => {
    const [automobiles, setAutomobiles] = useState<AutomobileResponse[]>([])
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState<string | null>(null)
    const [showForm, setShowForm] = useState(false)
    const [editingAutomobile, setEditingAutomobile] = useState<AutomobileResponse | null>(null)
    const [formData, setFormData] = useState<AutomobileCreate>({
        licensePlate: '',
        brand: '',
        model: '',
        year: new Date().getFullYear(),
        registration: '',
        dailyRate: 0
    })

    const automobileService = new AutomobileService()

     const loadAutomobiles = async () => {
        try {
            setLoading(true)
            setError(null)

            // CORRIGIDO: Buscar apenas os veículos do agente logado
            // O endpoint /automobiles retorna todos os veículos
            // Precisamos filtrar apenas os do agente atual
            const allAutomobiles = await automobileService.getAllAutomobiles()

            // O backend deve ter adicionado os campos createdByAgentUsername
            // Vamos buscar o username do token
            const token = localStorage.getItem('auth_token')
            if (!token) {
                setError('Token de autenticação não encontrado')
                setAutomobiles([])
                return
            }

            // Decodificar o token para pegar o username
            const base64Url = token.split('.')[1]
            const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/')
            const jsonPayload = decodeURIComponent(atob(base64).split('').map(function(c) {
                return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2)
            }).join(''))

            const payload = JSON.parse(jsonPayload)
            const currentUsername = payload.sub // O username está no campo 'sub' do JWT

            // Filtrar apenas os veículos criados pelo agente atual
            const myAutomobiles = allAutomobiles.filter(auto =>
                (auto as any).createdByAgentUsername === currentUsername
            )

            setAutomobiles(myAutomobiles)
        } catch (err) {
            console.error('Error loading automobiles:', err)
            setError('Erro ao carregar veículos. Verifique se você tem permissão de agente.')
        } finally {
            setLoading(false)
        }
    }

   useEffect(() => {
    loadAutomobiles()
}, [loadAutomobiles])

   

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault()
        try {
            setError(null)

            if (editingAutomobile) {
                await automobileService.updateAutomobile(editingAutomobile.id, formData)
            } else {
                await automobileService.createAutomobile(formData)
            }

            await loadAutomobiles()
            resetForm()
        } catch (err) {
            setError('Erro ao salvar veículo. Verifique os dados e sua permissão.')
            console.error('Error saving automobile:', err)
        }
    }

    const handleEdit = (automobile: AutomobileResponse) => {
        setEditingAutomobile(automobile)
        setFormData({
            licensePlate: automobile.licensePlate,
            brand: automobile.brand,
            model: automobile.model,
            year: automobile.year,
            registration: automobile.registration || '',
            dailyRate: automobile.dailyRate
        })
        setShowForm(true)
    }

    const handleDelete = async (id: string) => {
        if (!confirm('Tem certeza que deseja excluir este veículo?')) {
            return
        }

        try {
            setError(null)
            await automobileService.deleteAutomobile(id)
            await loadAutomobiles()
        } catch (err) {
            setError('Erro ao excluir veículo.')
            console.error('Error deleting automobile:', err)
        }
    }

    const resetForm = () => {
        setFormData({
            licensePlate: '',
            brand: '',
            model: '',
            year: new Date().getFullYear(),
            registration: '',
            dailyRate: 0
        })
        setEditingAutomobile(null)
        setShowForm(false)
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

    if (loading) {
        return (
            <div className={`${className} flex items-center justify-center py-12`}>
                <div className="text-white/80 text-lg">Carregando veículos...</div>
            </div>
        )
    }

    return (
        <div className={`${className} bg-white/10 backdrop-blur-md rounded-xl border border-white/20 p-6`}>
            <div className="flex justify-between items-center mb-6">
                <h2 className="text-2xl font-bold text-white flex items-center gap-2">
                    <Car className="w-6 h-6" />
                    Meus Veículos
                </h2>
                <button
                    onClick={() => setShowForm(true)}
                    className="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg flex items-center gap-2 transition-colors"
                >
                    <Plus className="w-4 h-4" />
                    Novo Veículo
                </button>
            </div>

            {error && (
                <div className="bg-red-500/20 border border-red-500/50 rounded-lg p-4 mb-6 flex items-center gap-2">
                    <AlertCircle className="w-5 h-5 text-red-400" />
                    <span className="text-red-200">{error}</span>
                </div>
            )}

            {showForm && (
                <div className="bg-white/5 backdrop-blur-sm rounded-lg p-6 mb-6 border border-white/10">
                    <h3 className="text-xl font-semibold text-white mb-4">
                        {editingAutomobile ? 'Editar Veículo' : 'Novo Veículo'}
                    </h3>

                    <form onSubmit={handleSubmit} className="space-y-4">
                        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                            <div>
                                <label className="block text-white/80 text-sm mb-2">
                                    Placa *
                                </label>
                                <input
                                    type="text"
                                    value={formData.licensePlate}
                                    onChange={(e) => setFormData({...formData, licensePlate: e.target.value})}
                                    className="w-full px-3 py-2 bg-white/10 border border-white/20 rounded-lg text-white placeholder-white/50 focus:outline-none focus:ring-2 focus:ring-blue-500"
                                    placeholder="ABC-1234"
                                    required
                                />
                            </div>

                            <div>
                                <label className="block text-white/80 text-sm mb-2">
                                    Marca *
                                </label>
                                <input
                                    type="text"
                                    value={formData.brand}
                                    onChange={(e) => setFormData({...formData, brand: e.target.value})}
                                    className="w-full px-3 py-2 bg-white/10 border border-white/20 rounded-lg text-white placeholder-white/50 focus:outline-none focus:ring-2 focus:ring-blue-500"
                                    placeholder="Toyota"
                                    required
                                />
                            </div>

                            <div>
                                <label className="block text-white/80 text-sm mb-2">
                                    Modelo *
                                </label>
                                <input
                                    type="text"
                                    value={formData.model}
                                    onChange={(e) => setFormData({...formData, model: e.target.value})}
                                    className="w-full px-3 py-2 bg-white/10 border border-white/20 rounded-lg text-white placeholder-white/50 focus:outline-none focus:ring-2 focus:ring-blue-500"
                                    placeholder="Corolla"
                                    required
                                />
                            </div>

                            <div>
                                <label className="block text-white/80 text-sm mb-2">
                                    Ano *
                                </label>
                                <input
                                    type="number"
                                    value={formData.year}
                                    onChange={(e) => setFormData({...formData, year: parseInt(e.target.value)})}
                                    className="w-full px-3 py-2 bg-white/10 border border-white/20 rounded-lg text-white placeholder-white/50 focus:outline-none focus:ring-2 focus:ring-blue-500"
                                    min="1900"
                                    max={new Date().getFullYear() + 1}
                                    required
                                />
                            </div>

                            <div>
                                <label className="block text-white/80 text-sm mb-2">
                                    Registro
                                </label>
                                <input
                                    type="text"
                                    value={formData.registration}
                                    onChange={(e) => setFormData({...formData, registration: e.target.value})}
                                    className="w-full px-3 py-2 bg-white/10 border border-white/20 rounded-lg text-white placeholder-white/50 focus:outline-none focus:ring-2 focus:ring-blue-500"
                                    placeholder="REG123456"
                                />
                            </div>

                            <div>
                                <label className="block text-white/80 text-sm mb-2">
                                    Taxa Diária (R$) *
                                </label>
                                <input
                                    type="number"
                                    step="0.01"
                                    min="0"
                                    value={formData.dailyRate}
                                    onChange={(e) => setFormData({...formData, dailyRate: parseFloat(e.target.value)})}
                                    className="w-full px-3 py-2 bg-white/10 border border-white/20 rounded-lg text-white placeholder-white/50 focus:outline-none focus:ring-2 focus:ring-blue-500"
                                    placeholder="150.00"
                                    required
                                />
                            </div>
                        </div>

                        <div className="flex gap-4 pt-4">
                            <button
                                type="submit"
                                className="bg-green-600 hover:bg-green-700 text-white px-6 py-2 rounded-lg transition-colors"
                            >
                                {editingAutomobile ? 'Atualizar' : 'Criar'} Veículo
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

            <div className="space-y-4">
                {automobiles.length === 0 ? (
                    <div className="text-center py-8 text-white/60">
                        Você ainda não cadastrou nenhum veículo.
                    </div>
                ) : (
                    automobiles.map((automobile) => (
                        <div
                            key={automobile.id}
                            className="bg-white/5 backdrop-blur-sm rounded-lg p-4 border border-white/10"
                        >
                            <div className="flex justify-between items-start">
                                <div className="flex-1">
                                    <div className="flex items-center gap-3 mb-2">
                                        <Car className="w-5 h-5 text-blue-400" />
                                        <h3 className="text-lg font-semibold text-white">
                                            {automobile.brand} {automobile.model}
                                        </h3>
                                        <span className={`px-2 py-1 rounded-full text-xs font-medium ${
                                            automobile.available
                                                ? 'bg-green-500/20 text-green-400 border border-green-500/30'
                                                : 'bg-red-500/20 text-red-400 border border-red-500/30'
                                        }`}>
                                            {automobile.available ? 'Disponível' : 'Indisponível'}
                                        </span>
                                    </div>

                                    <div className="grid grid-cols-2 md:grid-cols-4 gap-4 text-sm text-white/80">
                                        <div>
                                            <span className="font-medium">Placa:</span> {automobile.licensePlate}
                                        </div>
                                        <div>
                                            <span className="font-medium">Ano:</span> {automobile.year}
                                        </div>
                                        <div>
                                            <span className="font-medium">Taxa Diária:</span> {formatCurrency(automobile.dailyRate)}
                                        </div>
                                        <div>
                                            <span className="font-medium">Criado em:</span> {formatDate(automobile.createdAt)}
                                        </div>
                                    </div>

                                    {automobile.registration && (
                                        <div className="mt-2 text-sm text-white/70">
                                            <span className="font-medium">Registro:</span> {automobile.registration}
                                        </div>
                                    )}
                                </div>

                                <div className="flex gap-2 ml-4">
                                    <button
                                        onClick={() => handleEdit(automobile)}
                                        className="bg-blue-600 hover:bg-blue-700 text-white p-2 rounded-lg transition-colors"
                                        title="Editar"
                                    >
                                        <Edit className="w-4 h-4" />
                                    </button>
                                    <button
                                        onClick={() => handleDelete(automobile.id)}
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

export default AutomobileManagement