"use client"

import type React from "react"
import { useState, useEffect } from "react"
import { useRouter } from "next/navigation"
import { useAuth } from "@/shared/contexts/AuthContext"
import { ApiService } from "@/shared/services"
import { User, Eye, EyeOff, ArrowLeft } from "lucide-react"
import { safeString } from "@/shared/utils/type-guards"
import Link from "next/link"

const EditProfilePage: React.FC = () => {
    const router = useRouter()
    const { user } = useAuth()
    const [formData, setFormData] = useState({
        name: "",
        emailContact: "",
        currentPassword: "",
        newPassword: "",
        confirmPassword: ""
    })
    const [loading, setLoading] = useState(true)
    const [saving, setSaving] = useState(false)
    const [error, setError] = useState("")
    const [showCurrentPassword, setShowCurrentPassword] = useState(false)
    const [showNewPassword, setShowNewPassword] = useState(false)
    const [showConfirmPassword, setShowConfirmPassword] = useState(false)

    useEffect(() => {
        // Verificar se usuário está logado (qualquer tipo)
        if (!user) {
            router.push('/')
            return
        }

        const fetchUserData = async () => {
            try {
                setLoading(true)

                // Se for cliente, buscar dados do customer
                if (user.userType === 'cliente') {
                    try {
                        const customerData = await ApiService.customer.getCustomerById(user.id)
                        if (customerData) {
                            setFormData(prev => ({
                                ...prev,
                                name: safeString(customerData.name, user.name),
                                emailContact: safeString(customerData.emailContact, user.email)
                            }))
                        } else {
                            // Se não encontrar dados do cliente, usar dados do usuário
                            setFormData(prev => ({
                                ...prev,
                                name: user.name,
                                emailContact: user.email
                            }))
                        }
                    } catch (error) {
                        console.error("Error fetching customer data:", error)
                        // Usar dados do usuário como fallback
                        setFormData(prev => ({
                            ...prev,
                            name: user.name,
                            emailContact: user.email
                        }))
                    }
                } else {
                    // Para agentes, usar dados básicos do usuário
                    setFormData(prev => ({
                        ...prev,
                        name: user.name,
                        emailContact: user.email
                    }))
                }
            } catch (error) {
                console.error("Error loading user data:", error)
                setError("Erro ao carregar dados do perfil")
                // Usar dados básicos do usuário como fallback
                setFormData(prev => ({
                    ...prev,
                    name: user.name,
                    emailContact: user.email
                }))
            } finally {
                setLoading(false)
            }
        }

        fetchUserData()
    }, [user, router])

    const handleInputChange = (field: string, value: string) => {
        setFormData(prev => ({
            ...prev,
            [field]: value
        }))
    }

    const validateForm = (): boolean => {
        if (!formData.currentPassword.trim()) {
            setError("Senha atual é obrigatória")
            return false
        }

        if (!formData.newPassword.trim()) {
            setError("Nova senha é obrigatória")
            return false
        }

        if (formData.newPassword.length < 6) {
            setError("Nova senha deve ter pelo menos 6 caracteres")
            return false
        }

        if (formData.newPassword !== formData.confirmPassword) {
            setError("As senhas não coincidem")
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
            // Simular atualização de senha
            await new Promise(resolve => setTimeout(resolve, 1500))

            alert("Senha alterada com sucesso!")
            router.push('/')
        } catch (error) {
            console.error("Error updating password:", error)
            setError("Erro ao atualizar senha. Tente novamente.")
        } finally {
            setSaving(false)
        }
    }

    const handleGoBack = () => {
        router.push('/')
    }

    // const getUserTypeLabel = (userType: string) => {
    //     switch (userType) {
    //         case 'cliente':
    //             return 'Cliente'
    //         case 'agente-empresa':
    //             return 'Agente Empresa'
    //         case 'agente-banco':
    //             return 'Agente Banco'
    //         default:
    //             return 'Usuário'
    //     }
    // }

    // Se não há usuário, não renderizar nada (redirecionamento vai acontecer)
    if (!user) {
        return null
    }

    return (
        <div className="min-h-screen relative flex items-center justify-center p-4">
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

            {/* Conteúdo do formulário */}
            <div className="relative z-20 w-full max-w-md">
                {loading ? (
                    <div className="bg-white/10 backdrop-blur-md rounded-2xl shadow-2xl border border-white/20 p-8">
                        <div className="text-center">
                            <div className="w-12 h-12 border-4 border-white/30 border-t-white rounded-full animate-spin mx-auto mb-4"></div>
                            <div className="text-xl text-white drop-shadow-lg">Carregando dados do perfil...</div>
                        </div>
                    </div>
                ) : (
                    <div className="bg-white/10 backdrop-blur-md rounded-2xl shadow-2xl border border-white/20 p-8">
                        {/* Header com botão de voltar */}
                        <div className="flex items-center justify-between mb-6">
                            <button
                                onClick={handleGoBack}
                                className="text-white hover:bg-white/10 p-2 rounded-full transition-colors"
                            >
                                <ArrowLeft className="w-6 h-6" />
                            </button>
                            <div className="flex-1 text-center">
                                <div className="flex items-center justify-center mb-4">
                                    <div className="w-12 h-12 bg-transparent rounded-full flex items-center justify-center">
                                        <User className="text-blue-500 font-bold text-xl w-12 h-12" />
                                    </div>
                                </div>
                                <h1 className="text-3xl font-bold text-white drop-shadow-lg">Editar Perfil</h1>
                            </div>
                            <div className="w-10"></div> {/* Espaçador para centralizar */}
                        </div>

                        {error && (
                            <div className="mb-6 p-4 bg-red-500/20 backdrop-blur-sm border border-red-500/30 rounded-lg">
                                <p className="text-red-200 text-sm text-center">{error}</p>
                            </div>
                        )}

                        <form onSubmit={handleSubmit} className="space-y-4">
                            {/* Nome (somente leitura) */}
                            <div>
                                <label htmlFor="name" className="block text-sm font-medium text-white/90 mb-2">
                                    Nome
                                </label>
                                <input
                                    type="text"
                                    id="name"
                                    value={formData.name}
                                    readOnly
                                    className="w-full px-4 py-3 bg-white/5 backdrop-blur-sm border border-white/10 rounded-lg text-white/70 cursor-not-allowed"
                                />
                            </div>

                            {/* Email (somente leitura) */}
                            <div>
                                <label htmlFor="email" className="block text-sm font-medium text-white/90 mb-2">
                                    Email
                                </label>
                                <input
                                    type="email"
                                    id="email"
                                    value={formData.emailContact}
                                    readOnly
                                    className="w-full px-4 py-3 bg-white/5 backdrop-blur-sm border border-white/10 rounded-lg text-white/70 cursor-not-allowed"
                                />
                            </div>

                            {/* Senha atual */}
                            <div>
                                <label htmlFor="currentPassword" className="block text-sm font-medium text-white/90 mb-2">
                                    Senha Atual *
                                </label>
                                <div className="relative">
                                    <input
                                        type={showCurrentPassword ? "text" : "password"}
                                        id="currentPassword"
                                        value={formData.currentPassword}
                                        onChange={(e) => handleInputChange('currentPassword', e.target.value)}
                                        className="w-full px-4 py-3 pr-12 bg-white/10 backdrop-blur-sm border border-white/20 rounded-lg text-white placeholder-white/50 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all"
                                        placeholder="Digite sua senha atual"
                                        required
                                    />
                                    <button
                                        type="button"
                                        onClick={() => setShowCurrentPassword(!showCurrentPassword)}
                                        className="absolute right-3 top-1/2 -translate-y-1/2 text-white/50 hover:text-white transition-colors"
                                    >
                                        {showCurrentPassword ? <EyeOff className="w-5 h-5" /> : <Eye className="w-5 h-5" />}
                                    </button>
                                </div>
                            </div>

                            {/* Nova senha */}
                            <div>
                                <label htmlFor="newPassword" className="block text-sm font-medium text-white/90 mb-2">
                                    Nova Senha *
                                </label>
                                <div className="relative">
                                    <input
                                        type={showNewPassword ? "text" : "password"}
                                        id="newPassword"
                                        value={formData.newPassword}
                                        onChange={(e) => handleInputChange('newPassword', e.target.value)}
                                        className="w-full px-4 py-3 pr-12 bg-white/10 backdrop-blur-sm border border-white/20 rounded-lg text-white placeholder-white/50 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all"
                                        placeholder="Mínimo 6 caracteres"
                                        required
                                        minLength={6}
                                    />
                                    <button
                                        type="button"
                                        onClick={() => setShowNewPassword(!showNewPassword)}
                                        className="absolute right-3 top-1/2 -translate-y-1/2 text-white/50 hover:text-white transition-colors"
                                    >
                                        {showNewPassword ? <EyeOff className="w-5 h-5" /> : <Eye className="w-5 h-5" />}
                                    </button>
                                </div>
                            </div>

                            {/* Confirmar nova senha */}
                            <div>
                                <label htmlFor="confirmPassword" className="block text-sm font-medium text-white/90 mb-2">
                                    Confirmar Nova Senha *
                                </label>
                                <div className="relative">
                                    <input
                                        type={showConfirmPassword ? "text" : "password"}
                                        id="confirmPassword"
                                        value={formData.confirmPassword}
                                        onChange={(e) => handleInputChange('confirmPassword', e.target.value)}
                                        className={`w-full px-4 py-3 pr-12 bg-white/10 backdrop-blur-sm border rounded-lg text-white placeholder-white/50 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all ${
                                            formData.confirmPassword && formData.newPassword !== formData.confirmPassword
                                                ? 'border-red-500/50 bg-red-500/10'
                                                : formData.confirmPassword && formData.newPassword === formData.confirmPassword
                                                    ? 'border-green-500/50 bg-green-500/10'
                                                    : 'border-white/20'
                                        }`}
                                        placeholder="Digite a senha novamente"
                                        required
                                    />
                                    <button
                                        type="button"
                                        onClick={() => setShowConfirmPassword(!showConfirmPassword)}
                                        className="absolute right-3 top-1/2 -translate-y-1/2 text-white/50 hover:text-white transition-colors"
                                    >
                                        {showConfirmPassword ? <EyeOff className="w-5 h-5" /> : <Eye className="w-5 h-5" />}
                                    </button>
                                </div>
                                {formData.confirmPassword && formData.newPassword !== formData.confirmPassword && (
                                    <p className="text-red-300 text-xs mt-1">As senhas não coincidem</p>
                                )}
                                {formData.confirmPassword && formData.newPassword === formData.confirmPassword && (
                                    <p className="text-green-300 text-xs mt-1">✓ Senhas coincidem</p>
                                )}
                            </div>

                            {/* Botão de envio */}
                            <button
                                type="submit"
                                disabled={saving || !formData.currentPassword || !formData.newPassword || !formData.confirmPassword || formData.newPassword !== formData.confirmPassword}
                                className="w-full bg-gradient-to-r from-blue-600 to-blue-700 hover:from-blue-700 hover:to-blue-800 text-white py-3 px-6 rounded-lg font-medium transition-all duration-200 disabled:opacity-50 disabled:cursor-not-allowed focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 focus:ring-offset-transparent transform hover:scale-[1.02] active:scale-[0.98]"
                            >
                                {saving ? (
                                    <div className="flex items-center justify-center">
                                        <div className="w-5 h-5 border-2 border-white/30 border-t-white rounded-full animate-spin mr-2"></div>
                                        Alterando Senha...
                                    </div>
                                ) : (
                                    "Alterar Senha"
                                )}
                            </button>
                        </form>

                        {/* Links de navegação */}
                        <div className="mt-6 text-center">
                            <Link
                                href="/"
                                className="inline-flex items-center text-white/60 hover:text-white transition-colors text-sm group"
                            >
                                <svg className="w-4 h-4 mr-2 transform group-hover:-translate-x-1 transition-transform" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 19l-7-7 7-7" />
                                </svg>
                                Voltar ao início
                            </Link>
                        </div>
                    </div>
                )}
            </div>
        </div>
    )
}

export default EditProfilePage