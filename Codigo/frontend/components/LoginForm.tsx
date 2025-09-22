"use client"

import type React from "react"
import { useState } from "react"
import { useAuth } from "@/shared/contexts/AuthContext"
import { Eye, EyeOff, AlertCircle, Info } from "lucide-react"

interface LoginFormProps {
    onClose: () => void
    onSwitchToRegister: () => void
}

const LoginForm: React.FC<LoginFormProps> = ({ onClose, onSwitchToRegister }) => {
    const [email, setEmail] = useState("")
    const [password, setPassword] = useState("")
    const [showPassword, setShowPassword] = useState(false)
    const [isLoading, setIsLoading] = useState(false)
    const [error, setError] = useState("")

    const { login, addTestUsers } = useAuth() as any

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault()
        setIsLoading(true)
        setError("")

        // Validações básicas
        if (!email.trim() || !password.trim()) {
            setError("Email e senha são obrigatórios.")
            setIsLoading(false)
            return
        }

        if (!isValidEmail(email)) {
            setError("Formato de email inválido.")
            setIsLoading(false)
            return
        }

        try {
            const success = await login(email, password)
            if (success) {
                onClose()
            } else {
                setError("Credenciais inválidas. Verifique seu email e senha ou cadastre-se se ainda não tem uma conta.")
            }
        } catch (err) {
            setError("Erro ao fazer login. Tente novamente.")
        } finally {
            setIsLoading(false)
        }
    }

    const isValidEmail = (email: string): boolean => {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
        return emailRegex.test(email)
    }

    const handleAddTestUsers = () => {
        if (addTestUsers) {
            addTestUsers()
            alert("Usuários de teste adicionados! Agora você pode fazer login com:\n\n" +
                "Cliente: cliente@teste.com / 123456\n" +
                "Empresa: agente@teste.com / 123456\n" +
                "Banco: banco@teste.com / 123456")
        }
    }

    return (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center p-4 z-50">
            <div className="bg-white rounded-2xl shadow-2xl w-full max-w-md max-h-[90vh] overflow-y-auto">
                <div className="p-6">
                    <div className="flex justify-between items-center mb-6">
                        <div>
                            <h2 className="text-2xl font-bold text-gray-900">Entrar</h2>
                            <p className="text-gray-600 text-sm">Acesse sua conta</p>
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
                                <p className="text-red-800 text-sm font-medium">Erro no Login</p>
                                <p className="text-red-700 text-sm">{error}</p>
                            </div>
                        </div>
                    )}

                    <form onSubmit={handleSubmit} className="space-y-4">
                        <div>
                            <label htmlFor="email" className="block text-sm font-medium text-gray-700 mb-2">
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

                        <div>
                            <label htmlFor="password" className="block text-sm font-medium text-gray-700 mb-2">
                                Senha
                            </label>
                            <div className="relative">
                                <input
                                    type={showPassword ? "text" : "password"}
                                    id="password"
                                    value={password}
                                    onChange={(e) => setPassword(e.target.value)}
                                    className="w-full px-4 py-3 pr-12 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-colors"
                                    placeholder="••••••••"
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
                        </div>

                        <button
                            type="submit"
                            disabled={isLoading}
                            className="w-full bg-blue-600 text-white py-3 px-4 rounded-lg hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 disabled:opacity-50 disabled:cursor-not-allowed transition-colors font-medium"
                        >
                            {isLoading ? (
                                <div className="flex items-center justify-center gap-2">
                                    <div className="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin" />
                                    Entrando...
                                </div>
                            ) : (
                                "Entrar"
                            )}
                        </button>
                    </form>

                    {/* Informações para teste */}
                    <div className="mt-6 p-4 bg-blue-50 border border-blue-200 rounded-lg">
                        <div className="flex items-start gap-3">
                            <Info className="w-5 h-5 text-blue-500 mt-0.5 flex-shrink-0" />
                            <div>
                                <h4 className="text-sm font-medium text-blue-800 mb-2">
                                    Para Testar o Sistema
                                </h4>
                                <div className="text-xs text-blue-700 space-y-1">
                                    <p><strong>Cliente:</strong> cliente@teste.com / 123456</p>
                                    <p><strong>Empresa:</strong> agente@teste.com / 123456</p>
                                    <p><strong>Banco:</strong> banco@teste.com / 123456</p>
                                </div>
                                <button
                                    type="button"
                                    onClick={handleAddTestUsers}
                                    className="mt-2 px-3 py-1 bg-blue-600 text-white text-xs rounded hover:bg-blue-700 transition-colors"
                                >
                                    Criar Usuários de Teste
                                </button>
                            </div>
                        </div>
                    </div>

                    <div className="mt-6 text-center">
                        <p className="text-gray-600 text-sm">
                            Não tem uma conta?{" "}
                            <button
                                onClick={onSwitchToRegister}
                                className="text-blue-600 hover:text-blue-500 font-medium transition-colors"
                            >
                                Cadastre-se
                            </button>
                        </p>
                    </div>
                </div>
            </div>
        </div>
    )
}