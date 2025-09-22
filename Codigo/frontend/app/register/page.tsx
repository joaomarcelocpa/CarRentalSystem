"use client"

import type React from "react"
import { useState } from "react"
import { useAuth } from "@/shared/contexts/AuthContext"
import type { UserType } from "@/shared/interfaces/user"
import Link from "next/link"
import { useRouter } from "next/navigation"

const RegisterPage: React.FC = () => {
    const [name, setName] = useState("")
    const [email, setEmail] = useState("")
    const [password, setPassword] = useState("")
    const [confirmPassword, setConfirmPassword] = useState("")
    const [userType, setUserType] = useState<UserType>("cliente")
    const [isLoading, setIsLoading] = useState(false)
    const [error, setError] = useState("")

    const { register } = useAuth()
    const router = useRouter()

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault()
        setIsLoading(true)
        setError("")

        if (password !== confirmPassword) {
            setError("As senhas não coincidem.")
            setIsLoading(false)
            return
        }

        if (password.length < 6) {
            setError("A senha deve ter pelo menos 6 caracteres.")
            setIsLoading(false)
            return
        }

        try {
            const success = await register(name, email, password, userType)
            if (success) {
                router.push("/")
            } else {
                setError("Erro ao criar conta. Tente novamente.")
            }
        } catch (err) {
            setError("Erro ao criar conta. Tente novamente.")
        } finally {
            setIsLoading(false)
        }
    }

    return (
        <div className="min-h-screen relative flex items-center justify-center p-4">
            {/* Vídeo de fundo */}
            <video
                autoPlay
                loop
                muted
                playsInline
                className="absolute inset-0 w-full h-full object-cover z-0"
            >
                <source src="/wallpaper.mp4" type="video/mp4" />
            </video>

            {/* Overlay escuro */}
            <div className="absolute inset-0 bg-black/60 z-10"></div>

            {/* Conteúdo do registro */}
            <div className="relative z-20 w-full max-w-2xl">
                <div className="bg-white/10 backdrop-blur-md rounded-2xl shadow-2xl border border-white/20 p-8 lg:p-12">
                    {/* Logo/Título */}
                    <div className="text-center mb-8">
                        <div className="flex items-center justify-center mb-4">
                            <div className="w-12 h-12 bg-blue-600 rounded-full flex items-center justify-center">
                                <span className="text-white font-bold text-xl">R</span>
                            </div>
                        </div>
                        <h1 className="text-4xl font-bold text-white">Criar uma Conta</h1>
                        <p className="text-white/80 text-lg">Junte-se ao RentalCarSystem</p>
                    </div>

                    {error && (
                        <div className="mb-6 p-4 bg-red-500/20 backdrop-blur-sm border border-red-500/30 rounded-lg">
                            <p className="text-red-200 text-sm text-center">{error}</p>
                        </div>
                    )}

                    <form onSubmit={handleSubmit} className="space-y-6">
                        {/* Nome completo */}
                        <div>
                            <label htmlFor="name" className="block text-sm font-medium text-white/90 mb-2">
                                Nome Completo
                            </label>
                            <input
                                type="text"
                                id="name"
                                value={name}
                                onChange={(e) => setName(e.target.value)}
                                className="w-full px-4 py-3 bg-white/10 backdrop-blur-sm border border-white/20 rounded-lg text-white placeholder-white/50 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all text-base"
                                placeholder="Digite seu nome completo"
                                required
                            />
                        </div>

                        {/* Email */}
                        <div>
                            <label htmlFor="email" className="block text-sm font-medium text-white/90 mb-2">
                                Email
                            </label>
                            <input
                                type="email"
                                id="email"
                                value={email}
                                onChange={(e) => setEmail(e.target.value)}
                                className="w-full px-4 py-3 bg-white/10 backdrop-blur-sm border border-white/20 rounded-lg text-white placeholder-white/50 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all text-base"
                                placeholder="seu@email.com"
                                required
                            />
                        </div>

                        {/* Grid para senhas lado a lado em telas maiores */}
                        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                            <div>
                                <label htmlFor="password" className="block text-sm font-medium text-white/90 mb-2">
                                    Senha
                                </label>
                                <input
                                    type="password"
                                    id="password"
                                    value={password}
                                    onChange={(e) => setPassword(e.target.value)}
                                    className="w-full px-4 py-3 bg-white/10 backdrop-blur-sm border border-white/20 rounded-lg text-white placeholder-white/50 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all text-base"
                                    placeholder="Mínimo 6 caracteres"
                                    required
                                    minLength={6}
                                />
                            </div>

                            <div>
                                <label htmlFor="confirmPassword" className="block text-sm font-medium text-white/90 mb-2">
                                    Confirmar Senha
                                </label>
                                <input
                                    type="password"
                                    id="confirmPassword"
                                    value={confirmPassword}
                                    onChange={(e) => setConfirmPassword(e.target.value)}
                                    className="w-full px-4 py-3 bg-white/10 backdrop-blur-sm border border-white/20 rounded-lg text-white placeholder-white/50 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all text-base"
                                    placeholder="Digite a senha novamente"
                                    required
                                    minLength={6}
                                />
                            </div>
                        </div>

                        {/* Tipo de usuário */}
                        <div>
                            <label htmlFor="userType" className="block text-sm font-medium text-white/90 mb-2">
                                Tipo de Usuário
                            </label>
                            <select
                                id="userType"
                                value={userType}
                                onChange={(e) => setUserType(e.target.value as UserType)}
                                className="w-full px-4 py-3 bg-white/10 backdrop-blur-sm border border-white/20 rounded-lg text-white focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all text-base"
                            >
                                <option value="cliente" className="bg-gray-800 text-white">Cliente</option>
                                <option value="agente-empresa" className="bg-gray-800 text-white">Agente Empresa</option>
                                <option value="agente-banco" className="bg-gray-800 text-white">Agente Banco</option>
                            </select>
                        </div>

                        {/* Botão de envio */}
                        <button
                            type="submit"
                            disabled={isLoading}
                            className="w-full bg-gradient-to-r from-blue-600 to-blue-700 hover:from-blue-700 hover:to-blue-800 text-white py-4 px-6 rounded-lg font-medium text-lg transition-all duration-200 disabled:opacity-50 disabled:cursor-not-allowed focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 focus:ring-offset-transparent transform hover:scale-[1.02] active:scale-[0.98]"
                        >
                            {isLoading ? (
                                <div className="flex items-center justify-center">
                                    <div className="w-5 h-5 border-2 border-white/30 border-t-white rounded-full animate-spin mr-2"></div>
                                    Criando conta...
                                </div>
                            ) : (
                                "Criar Conta"
                            )}
                        </button>
                    </form>

                    {/* Links de navegação */}
                    <div className="mt-8 text-center space-y-4">
                        <p className="text-white/80 text-base">
                            Já tem uma conta?{" "}
                            <Link
                                href="/login"
                                className="text-blue-400 hover:text-blue-300 font-medium transition-colors underline decoration-2 underline-offset-2"
                            >
                                Faça login
                            </Link>
                        </p>
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
            </div>
        </div>
    )
}

export default RegisterPage