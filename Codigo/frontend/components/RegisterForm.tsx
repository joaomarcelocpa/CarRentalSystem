/* eslint-disable @typescript-eslint/no-explicit-any */
"use client"

import type React from "react"
import { useState } from "react"
import { useAuth } from "@/shared/contexts/AuthContext"
import type { UserType } from "@/shared/interfaces/user"
import { Eye, EyeOff, AlertCircle, CheckCircle, User, Mail, Lock, Shield } from "lucide-react"

interface RegisterFormProps {
    onClose: () => void
    onSwitchToLogin: () => void
}

const RegisterForm: React.FC<RegisterFormProps> = ({ onClose, onSwitchToLogin }) => {
    const [name, setName] = useState("")
    const [email, setEmail] = useState("")
    const [password, setPassword] = useState("")
    const [confirmPassword, setConfirmPassword] = useState("")
    const [userType, setUserType] = useState<UserType>("cliente")
    const [showPassword, setShowPassword] = useState(false)
    const [showConfirmPassword, setShowConfirmPassword] = useState(false)
    const [isLoading, setIsLoading] = useState(false)
    const [error, setError] = useState("")

    const { register } = useAuth()

    const validateForm = (): string | null => {
        if (!name.trim()) {
            return "Nome é obrigatório"
        }

        if (name.trim().length < 2) {
            return "Nome deve ter pelo menos 2 caracteres"
        }

        if (!email.trim()) {
            return "Email é obrigatório"
        }

        if (!isValidEmail(email)) {
            return "Formato de email inválido"
        }

        if (!password) {
            return "Senha é obrigatória"
        }

        if (password.length < 6) {
            return "Senha deve ter pelo menos 6 caracteres"
        }

        if (password !== confirmPassword) {
            return "As senhas não coincidem"
        }

        return null
    }

    const isValidEmail = (email: string): boolean => {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
        return emailRegex.test(email)
    }

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault()
        setError("")

        const validationError = validateForm()
        if (validationError) {
            setError(validationError)
            return
        }

        setIsLoading(true)

        try {
            const success = await register(name.trim(), email.trim(), password, userType)
            if (success) {
                onClose()
            }
        } catch (err: any) {
            setError(err.message || "Erro ao criar conta. Tente novamente.")
        } finally {
            setIsLoading(false)
        }
    }

    const getPasswordStrength = (password: string): { score: number; text: string; color: string } => {
        if (password.length < 6) return { score: 0, text: "Muito fraca", color: "text-red-500" }
        if (password.length < 8) return { score: 1, text: "Fraca", color: "text-orange-500" }
        if (password.length < 12 && /[A-Z]/.test(password) && /[0-9]/.test(password)) {
            return { score: 2, text: "Média", color: "text-yellow-500" }
        }
        if (password.length >= 12 && /[A-Z]/.test(password) && /[0-9]/.test(password) && /[^A-Za-z0-9]/.test(password)) {
            return { score: 3, text: "Forte", color: "text-green-500" }
        }
        return { score: 1, text: "Fraca", color: "text-orange-500" }
    }

    const passwordStrength = getPasswordStrength(password)

    const getUserTypeInfo = (type: UserType) => {
        switch (type) {
            case 'cliente':
                return {
                    title: 'Cliente',
                    description: 'Pode solicitar aluguel de veículos',
                    icon: '👤',
                    features: ['Solicitar aluguel', 'Ver pedidos', 'Gerenciar perfil']
                }
            case 'agente-empresa':
                return {
                    title: 'Agente Empresa',
                    description: 'Pode gerenciar e avaliar solicitações',
                    icon: '🏢',
                    features: ['Avaliar pedidos', 'Modificar solicitações', 'Gerenciar frota']
                }
            case 'agente-banco':
                return {
                    title: 'Agente Banco',
                    description: 'Pode avaliar e conceder crédito',
                    icon: '🏦',
                    features: ['Avaliar crédito', 'Contratos financeiros', 'Análise de risco']
                }
        }
    }

    const currentUserTypeInfo = getUserTypeInfo(userType)

    return (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center p-4 z-50">
            <div className="bg-white rounded-2xl shadow-2xl w-full max-w-lg max-h-[95vh] overflow-y-auto">
                <div className="p-6">
                    <div className="flex justify-between items-center mb-6">
                        <div>
                            <h2 className="text-2xl font-bold text-gray-900">Criar Conta</h2>
                            <p className="text-gray-600 text-sm">Cadastre-se no RentalCarSystem</p>
                        </div>
                        <button
                            onClick={onClose}
                            className="text-gray-400 hover:text-gray-600 transition-colors p-1"
                        >
                            <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                            </svg>
                        </button>
                    </div>

                    {error && (
                        <div className="mb-4 p-4 bg-red-50 border border-red-200 rounded-lg flex items-start gap-3">
                            <AlertCircle className="w-5 h-5 text-red-500 mt-0.5 flex-shrink-0" />
                            <div>
                                <p className="text-red-800 text-sm font-medium">Erro no Cadastro</p>
                                <p className="text-red-700 text-sm">{error}</p>
                            </div>
                        </div>
                    )}

                    <form onSubmit={handleSubmit} className="space-y-4">
                        {/* Nome */}
                        <div>
                            <label htmlFor="name" className="block text-sm font-medium text-gray-700 mb-2">
                                <User className="w-4 h-4 inline mr-2" />
                                Nome Completo
                            </label>
                            <input
                                type="text"
                                id="name"
                                value={name}
                                onChange={(e) => setName(e.target.value)}
                                className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-colors"
                                placeholder="Digite seu nome completo"
                                required
                            />
                        </div>

                        {/* Email */}
                        <div>
                            <label htmlFor="email" className="block text-sm font-medium text-gray-700 mb-2">
                                <Mail className="w-4 h-4 inline mr-2" />
                                Email
                            </label>
                            <input
                                type="email"
                                id="email"
                                value={email}
                                onChange={(e) => setEmail(e.target.value)}
                                className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-colors"
                                placeholder="seu@email.com"
                                required
                            />
                        </div>

                        {/* Tipo de Usuário */}
                        <div>
                            <label htmlFor="userType" className="block text-sm font-medium text-gray-700 mb-2">
                                <Shield className="w-4 h-4 inline mr-2" />
                                Tipo de Usuário
                            </label>
                            <select
                                id="userType"
                                value={userType}
                                onChange={(e) => setUserType(e.target.value as UserType)}
                                className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-colors bg-white"
                            >
                                <option value="cliente">Cliente</option>
                                <option value="agente-empresa">Agente Empresa</option>
                                <option value="agente-banco">Agente Banco</option>
                            </select>
                        </div>

                        {/* Informações do tipo selecionado */}
                        <div className="bg-blue-50 border border-blue-200 rounded-lg p-4">
                            <div className="flex items-start gap-3">
                                <span className="text-2xl">{currentUserTypeInfo.icon}</span>
                                <div className="flex-1">
                                    <h4 className="text-sm font-medium text-blue-800 mb-1">
                                        {currentUserTypeInfo.title}
                                    </h4>
                                    <p className="text-xs text-blue-700 mb-2">
                                        {currentUserTypeInfo.description}
                                    </p>
                                    <div className="space-y-1">
                                        <p className="text-xs text-blue-600 font-medium">Funcionalidades:</p>
                                        <ul className="text-xs text-blue-600 space-y-0.5">
                                            {currentUserTypeInfo.features.map((feature, index) => (
                                                <li key={index} className="flex items-center gap-1">
                                                    <CheckCircle className="w-3 h-3" />
                                                    {feature}
                                                </li>
                                            ))}
                                        </ul>
                                    </div>
                                </div>
                            </div>
                        </div>

                        {/* Senha */}
                        <div>
                            <label htmlFor="password" className="block text-sm font-medium text-gray-700 mb-2">
                                <Lock className="w-4 h-4 inline mr-2" />
                                Senha
                            </label>
                            <div className="relative">
                                <input
                                    type={showPassword ? "text" : "password"}
                                    id="password"
                                    value={password}
                                    onChange={(e) => setPassword(e.target.value)}
                                    className="w-full px-4 py-3 pr-12 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-colors"
                                    placeholder="Mínimo 6 caracteres"
                                    required
                                />
                                <button
                                    type="button"
                                    onClick={() => setShowPassword(!showPassword)}
                                    className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600 transition-colors"
                                >
                                    {showPassword ? <EyeOff className="w-5 h-5" /> : <Eye className="w-5 h-5" />}
                                </button>
                            </div>
                            {password && (
                                <div className="mt-2 flex items-center gap-2">
                                    <div className="flex-1 bg-gray-200 rounded-full h-2">
                                        <div
                                            className={`h-2 rounded-full transition-all ${
                                                passwordStrength.score === 0 ? 'bg-red-500 w-1/4' :
                                                    passwordStrength.score === 1 ? 'bg-orange-500 w-2/4' :
                                                        passwordStrength.score === 2 ? 'bg-yellow-500 w-3/4' :
                                                            'bg-green-500 w-full'
                                            }`}
                                        />
                                    </div>
                                    <span className={`text-xs ${passwordStrength.color}`}>
                                        {passwordStrength.text}
                                    </span>
                                </div>
                            )}
                        </div>

                        {/* Confirmar Senha */}
                        <div>
                            <label htmlFor="confirmPassword" className="block text-sm font-medium text-gray-700 mb-2">
                                <Lock className="w-4 h-4 inline mr-2" />
                                Confirmar Senha
                            </label>
                            <div className="relative">
                                <input
                                    type={showConfirmPassword ? "text" : "password"}
                                    id="confirmPassword"
                                    value={confirmPassword}
                                    onChange={(e) => setConfirmPassword(e.target.value)}
                                    className={`w-full px-4 py-3 pr-12 border rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-colors ${
                                        confirmPassword && password !== confirmPassword
                                            ? 'border-red-300 bg-red-50'
                                            : confirmPassword && password === confirmPassword
                                                ? 'border-green-300 bg-green-50'
                                                : 'border-gray-300'
                                    }`}
                                    placeholder="Digite a senha novamente"
                                    required
                                />
                                <button
                                    type="button"
                                    onClick={() => setShowConfirmPassword(!showConfirmPassword)}
                                    className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600 transition-colors"
                                >
                                    {showConfirmPassword ? <EyeOff className="w-5 h-5" /> : <Eye className="w-5 h-5" />}
                                </button>
                            </div>
                            {confirmPassword && password !== confirmPassword && (
                                <p className="text-red-500 text-xs mt-1">As senhas não coincidem</p>
                            )}
                            {confirmPassword && password === confirmPassword && (
                                <p className="text-green-500 text-xs mt-1 flex items-center gap-1">
                                    <CheckCircle className="w-3 h-3" />
                                    Senhas coincidem
                                </p>
                            )}
                        </div>

                        {/* Botão de envio */}
                        <button
                            type="submit"
                            disabled={isLoading || !password || !confirmPassword || password !== confirmPassword}
                            className="w-full bg-blue-600 text-white py-3 px-4 rounded-lg hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 disabled:opacity-50 disabled:cursor-not-allowed transition-colors font-medium"
                        >
                            {isLoading ? (
                                <div className="flex items-center justify-center gap-2">
                                    <div className="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin" />
                                    Criando conta...
                                </div>
                            ) : (
                                "Criar Conta"
                            )}
                        </button>
                    </form>

                    {/* Informações sobre requisitos */}
                    <div className="mt-6 p-4 bg-gray-50 border border-gray-200 rounded-lg">
                        <h4 className="text-sm font-medium text-gray-800 mb-2">Requisitos da Conta</h4>
                        <ul className="text-xs text-gray-600 space-y-1">
                            <li className="flex items-center gap-2">
                                {name.length >= 2 ? <CheckCircle className="w-3 h-3 text-green-500" /> : <div className="w-3 h-3 border border-gray-400 rounded-full" />}
                                Nome com pelo menos 2 caracteres
                            </li>
                            <li className="flex items-center gap-2">
                                {isValidEmail(email) ? <CheckCircle className="w-3 h-3 text-green-500" /> : <div className="w-3 h-3 border border-gray-400 rounded-full" />}
                                Email válido
                            </li>
                            <li className="flex items-center gap-2">
                                {password.length >= 6 ? <CheckCircle className="w-3 h-3 text-green-500" /> : <div className="w-3 h-3 border border-gray-400 rounded-full" />}
                                Senha com pelo menos 6 caracteres
                            </li>
                            <li className="flex items-center gap-2">
                                {password && confirmPassword && password === confirmPassword ? <CheckCircle className="w-3 h-3 text-green-500" /> : <div className="w-3 h-3 border border-gray-400 rounded-full" />}
                                Senhas coincidem
                            </li>
                        </ul>
                    </div>

                    <div className="mt-6 text-center">
                        <p className="text-gray-600 text-sm">
                            Já tem uma conta?{" "}
                            <button
                                onClick={onSwitchToLogin}
                                className="text-blue-600 hover:text-blue-500 font-medium transition-colors"
                            >
                                Faça login
                            </button>
                        </p>
                    </div>
                </div>
            </div>
        </div>
    )
}

export default RegisterForm