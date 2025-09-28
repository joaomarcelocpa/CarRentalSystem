"use client"

import React from 'react'
import { useRouter } from 'next/navigation'
import Header from '@/components/Header'
import AutomobileManagement from '@/components/AutomobileManagement'
import { useAuth } from '@/shared/contexts/AuthContext'
import { ArrowLeft } from 'lucide-react'

const AutomobileManagementPage: React.FC = () => {
    const { user } = useAuth()
    const router = useRouter()

    // Verificar se o usuário é um agente
    const isAgent = user?.userType === 'agente-empresa' || user?.userType === 'agente-banco'

    if (!user) {
        router.push('/login')
        return null
    }

    if (!isAgent) {
        return (
            <div className="min-h-screen relative">
                <video
                    autoPlay
                    loop
                    muted
                    playsInline
                    className="fixed inset-0 w-full h-full object-cover z-0"
                >
                    <source src="/wallpaper.mp4" type="video/mp4" />
                </video>
                <div className="fixed inset-0 bg-black/40 z-10"></div>
                
                <div className="relative z-20">
                    <Header />
                    <main className="container mx-auto px-4 py-8">
                        <div className="max-w-2xl mx-auto bg-red-500/20 backdrop-blur-md rounded-xl border border-red-500/50 p-8 text-center">
                            <h1 className="text-2xl font-bold text-white mb-4">Acesso Negado</h1>
                            <p className="text-red-200 mb-6">
                                Apenas agentes autorizados podem acessar o gerenciamento de veículos.
                            </p>
                            <button
                                onClick={() => router.push('/')}
                                className="bg-blue-600 hover:bg-blue-700 text-white px-6 py-2 rounded-lg transition-colors"
                            >
                                Voltar ao Início
                            </button>
                        </div>
                    </main>
                </div>
            </div>
        )
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
                <Header />
                
                <main className="container mx-auto px-4 py-8">
                    <div className="max-w-6xl mx-auto">
                        {/* Botão de voltar */}
                        <button
                            onClick={() => router.push('/')}
                            className="flex items-center gap-2 text-white/80 hover:text-white mb-6 transition-colors"
                        >
                            <ArrowLeft className="w-5 h-5" />
                            Voltar ao Painel
                        </button>

                        {/* Componente de gerenciamento */}
                        <AutomobileManagement />
                    </div>
                </main>
            </div>
        </div>
    )
}

export default AutomobileManagementPage
