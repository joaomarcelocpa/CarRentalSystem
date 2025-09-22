"use client"

import type React from "react"
import Header from "@/components/Header"
import CarCarousel from "@/components/CarCarousel"
import ActionButtons from "@/components/ActionButtons"
import { useAuth } from "@/shared/contexts/AuthContext"
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
                            <div className="text-center py-10 bg-white/10 backdrop-blur-md rounded-xl border border-white/20">
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
                                            className="px-8 py-3 bg-blue-600 hover:bg-blue-700 text-white rounded-lg transition-colors font-medium text-lg"
                                        >
                                            Criar Conta
                                        </button>
                                    </div>
                                </div>
                            </div>
                        )}
                    </div>
                </main>

                {/* Footer */}
                <footer className="bg-transparent py-8 mt-16">
                    <div className="container mx-auto px-4 text-center">
                        <p className="text-white/60">© 2024 RentalCarSystem. Todos os direitos reservados.</p>
                    </div>
                </footer>
            </div>
        </div>
    )
}

export default HomePage