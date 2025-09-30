"use client"

import type React from "react"
import { useState } from "react"
import { useAuth } from "@/shared/contexts/AuthContext"
import Link from "next/link"
import { useRouter } from "next/navigation"

const LoginPage: React.FC = () => {
    const [email, setEmail] = useState("")
    const [password, setPassword] = useState("")
    const [isLoading, setIsLoading] = useState(false)
    const [error, setError] = useState("")

    const { login } = useAuth()
    const router = useRouter()

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault()
        setIsLoading(true)
        setError("")

        try {
            const success = await login(email, password)
            if (success) {
                router.push("/")
            } else {
                setError("Credenciais inválidas. Tente novamente.")
            }
        } catch {
            setError("Erro ao fazer login. Tente novamente.")
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

            {/* Conteúdo do login */}
            <div className="relative z-20 w-full max-w-md">
                <div className="bg-white/10 backdrop-blur-md rounded-2xl shadow-2xl border border-white/20 p-8">
                    {/* Logo/Título */}
                    <div className="text-center mb-8">
                        <div className="flex items-center justify-center mb-4">
                            <div className="w-12 h-12 bg-blue-600 rounded-full flex items-center justify-center">
                                <span className="text-white font-bold text-xl">R</span>
                            </div>
                        </div>
                        <h1 className="text-3xl font-bold text-white mb-2">Login</h1>
                        <p className="text-white/80">Entre na sua conta</p>
                    </div>

                    {error && (
                        <div className="mb-6 p-4 bg-red-500/20 backdrop-blur-sm border border-red-500/30 rounded-lg">
                            <p className="text-red-200 text-sm text-center">{error}</p>
                        </div>
                    )}

                    <form onSubmit={handleSubmit} className="space-y-6">
                        <div>
                            <label htmlFor="email" className="block text-sm font-medium text-white/90 mb-2">
                                Username
                            </label>
                            <input
                                type="text"
                                id="email"
                                value={email}
                                onChange={(e) => setEmail(e.target.value)}
                                className="w-full px-4 py-3 bg-white/10 backdrop-blur-sm border border-white/20 rounded-lg text-white placeholder-white/50 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all"
                                placeholder="cliente.teste"
                                required
                            />
                        </div>

                        <div>
                            <label htmlFor="password" className="block text-sm font-medium text-white/90 mb-2">
                                Senha
                            </label>
                            <input
                                type="password"
                                id="password"
                                value={password}
                                onChange={(e) => setPassword(e.target.value)}
                                className="w-full px-4 py-3 bg-white/10 backdrop-blur-sm border border-white/20 rounded-lg text-white placeholder-white/50 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all"
                                placeholder="••••••••"
                                required
                            />
                        </div>

                        <button
                            type="submit"
                            disabled={isLoading}
                            className="w-full bg-blue-600 hover:bg-blue-700 text-white py-3 px-4 rounded-lg font-medium transition-colors disabled:opacity-50 disabled:cursor-not-allowed focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 focus:ring-offset-transparent"
                        >
                            {isLoading ? (
                                <div className="flex items-center justify-center">
                                    <div className="w-5 h-5 border-2 border-white/30 border-t-white rounded-full animate-spin mr-2"></div>
                                    Entrando...
                                </div>
                            ) : (
                                "Entrar"
                            )}
                        </button>
                    </form>

                    <div className="mt-8 text-center">
                        <p className="text-white/80 text-sm">
                            Não tem uma conta?{" "}
                            <Link
                                href="/register"
                                className="text-blue-400 hover:text-blue-300 font-medium transition-colors"
                            >
                                Cadastre-se
                            </Link>
                        </p>
                        <Link
                            href="/"
                            className="inline-block mt-4 text-white/60 hover:text-white transition-colors text-sm"
                        >
                            ← Voltar ao início
                        </Link>
                    </div>
                </div>
            </div>
        </div>
    )
}

export default LoginPage