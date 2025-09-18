"use client"

import type React from "react"
import Header from "../../components/Header"
import CarCarousel from "../../components/CarCarousel"
import ActionButtons from "../../components/ActionButtons"
import { useAuth } from "../../contexts/AuthContext"
import { useRouter } from "next/navigation"

const HomePage: React.FC = () => {
    const { user } = useAuth()
    const router = useRouter()

    const handleLoginClick = () => {
        router.push("/login")
    }

    const handleRegisterClick = () => {
        router.push("/register")
    }

    return (
        <div className="min-h-screen relative">
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
            <div className="fixed inset-0 bg-black/40 z-10"></div>

            {/* Conteúdo da página */}
            <div className="relative z-20">
                <Header onLoginClick={handleLoginClick} onRegisterClick={handleRegisterClick} />

                <main className="container mx-auto px-4 py-8">
                    {/* Hero Section */}
                    <div className="text-center mb-12">
                        <h1 className="text-4xl md:text-6xl font-bold text-white mb-4 text-balance drop-shadow-lg">
                            Alugue o Carro dos Seus Sonhos
                        </h1>
                        <p className="text-xl text-white/90 max-w-2xl mx-auto text-pretty drop-shadow-md">
                            Encontre o veículo perfeito para sua jornada. Carros modernos, preços justos e atendimento excepcional.
                        </p>
                    </div>

                    {/* Car Carousel */}
                    <div className="mb-16">
                        <CarCarousel />
                    </div>

                    {/* Action Buttons or Welcome Message */}
                    <div className="mb-12">
                        {user ? (
                            <ActionButtons />
                        ) : (
                            <div className="text-center py-12 bg-white/10 backdrop-blur-md rounded-xl border border-white/20">
                                <div className="max-w-2xl mx-auto px-6">
                                    <h2 className="text-3xl font-bold text-white mb-4">Bem-vindo ao RentalCarSystem</h2>
                                    <p className="text-white/80 text-lg mb-8">
                                        Para acessar todas as funcionalidades do nosso sistema de aluguel de carros, faça login ou crie sua
                                        conta gratuitamente.
                                    </p>
                                    <div className="flex flex-col sm:flex-row gap-4 justify-center">
                                        <button
                                            onClick={handleLoginClick}
                                            className="px-8 py-3 bg-blue-600 hover:bg-blue-700 text-white rounded-lg transition-colors font-medium text-lg"
                                        >
                                            Fazer Login
                                        </button>
                                        <button
                                            onClick={handleRegisterClick}
                                            className="px-8 py-3 border-2 border-blue-600 text-blue-400 hover:bg-blue-600 hover:text-white rounded-lg transition-colors font-medium text-lg"
                                        >
                                            Criar Conta
                                        </button>
                                    </div>
                                </div>
                            </div>
                        )}
                    </div>

                    {/* Features Section */}
                    <div className="grid grid-cols-1 md:grid-cols-3 gap-8 mb-16">
                        <div className="text-center p-6 bg-white/10 backdrop-blur-md rounded-xl border border-white/20">
                            <div className="w-16 h-16 bg-blue-600/20 backdrop-blur-sm rounded-full flex items-center justify-center mx-auto mb-4">
                                <svg className="w-8 h-8 text-blue-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path
                                        strokeLinecap="round"
                                        strokeLinejoin="round"
                                        strokeWidth={2}
                                        d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"
                                    />
                                </svg>
                            </div>
                            <h3 className="text-xl font-semibold text-white mb-2">Processo Simples</h3>
                            <p className="text-white/80">
                                Alugue seu carro em poucos cliques com nosso sistema intuitivo e seguro.
                            </p>
                        </div>

                        <div className="text-center p-6 bg-white/10 backdrop-blur-md rounded-xl border border-white/20">
                            <div className="w-16 h-16 bg-blue-600/20 backdrop-blur-sm rounded-full flex items-center justify-center mx-auto mb-4">
                                <svg className="w-8 h-8 text-blue-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path
                                        strokeLinecap="round"
                                        strokeLinejoin="round"
                                        strokeWidth={2}
                                        d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1"
                                    />
                                </svg>
                            </div>
                            <h3 className="text-xl font-semibold text-white mb-2">Preços Justos</h3>
                            <p className="text-white/80">Oferecemos as melhores tarifas do mercado sem taxas ocultas.</p>
                        </div>

                        <div className="text-center p-6 bg-white/10 backdrop-blur-md rounded-xl border border-white/20">
                            <div className="w-16 h-16 bg-blue-600/20 backdrop-blur-sm rounded-full flex items-center justify-center mx-auto mb-4">
                                <svg className="w-8 h-8 text-blue-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path
                                        strokeLinecap="round"
                                        strokeLinejoin="round"
                                        strokeWidth={2}
                                        d="M18.364 5.636l-3.536 3.536m0 5.656l3.536 3.536M9.172 9.172L5.636 5.636m3.536 9.192L5.636 18.364M12 2.25a9.75 9.75 0 100 19.5 9.75 9.75 0 000-19.5z"
                                    />
                                </svg>
                            </div>
                            <h3 className="text-xl font-semibold text-white mb-2">Suporte 24/7</h3>
                            <p className="text-white/80">
                                Nossa equipe está sempre disponível para ajudá-lo em qualquer situação.
                            </p>
                        </div>
                    </div>
                </main>

                {/* Footer */}
                <footer className="bg-black/30 backdrop-blur-md py-8 mt-16">
                    <div className="container mx-auto px-4 text-center">
                        <p className="text-white/60">© 2024 RentalCarSystem. Todos os direitos reservados.</p>
                    </div>
                </footer>
            </div>
        </div>
    )
}

export default HomePage