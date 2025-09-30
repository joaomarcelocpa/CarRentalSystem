/* eslint-disable @next/next/no-img-element */
"use client"

import type React from "react"
import { useState, useEffect } from "react"
import { useRouter } from "next/navigation"
import { useAuth } from "@/shared/contexts/AuthContext"
import { ApiService } from "@/shared/services"
import type { Automobile } from "@/shared/types/automobile"
import { formatCurrency, safeNumber } from "@/shared/utils/type-guards"
import { Car, Fuel, Users, Cog, Info, ArrowLeft } from "lucide-react"
import RentalConfirmationModal from "@/components/RentalConfirmationModal"

const CarSelectionPage: React.FC = () => {
    const router = useRouter()
    const { user } = useAuth()
    const [cars, setCars] = useState<Automobile[]>([])
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState<string | null>(null)
    const [selectedCategory, setSelectedCategory] = useState<string>("all")
    const [selectedCar, setSelectedCar] = useState<Automobile | null>(null)
    const [showConfirmationModal, setShowConfirmationModal] = useState(false)

    useEffect(() => {
        // Verificar se usuário está logado e é cliente
        if (!user || user.userType !== 'cliente') {
            router.push('/')
            return
        }

        const fetchCars = async () => {
            try {
                setLoading(true)
                const availableCars = await ApiService.automobile.getAvailableAutomobiles()

                if (availableCars.length === 0) {
                    // Mock data para demonstração
                    const mockCars: Automobile[] = [
                        {
                            id: "mock-1",
                            brand: "Toyota",
                            model: "Corolla",
                            year: 2023,
                            dailyRate: 120,
                            available: true,
                        },
                        {
                            id: "mock-2",
                            brand: "Honda",
                            model: "Civic",
                            year: 2023,
                            dailyRate: 130,
                            available: true,
                        },
                        {
                            id: "mock-3",
                            brand: "Volkswagen",
                            model: "Jetta",
                            year: 2022,
                            dailyRate: 110,
                            available: true,
                        },
                        {
                            id: "mock-4",
                            brand: "Hyundai",
                            model: "Elantra",
                            year: 2023,
                            dailyRate: 115,
                            available: true,
                        },
                        {
                            id: "mock-5",
                            brand: "BMW",
                            model: "320i",
                            year: 2023,
                            dailyRate: 250,
                            available: true,
                        },
                        {
                            id: "mock-6",
                            brand: "Mercedes",
                            model: "C200",
                            year: 2023,
                            dailyRate: 280,
                            available: true,
                        },
                        {
                            id: "mock-7",
                            brand: "Ford",
                            model: "Ka",
                            year: 2022,
                            dailyRate: 85,
                            available: true,
                        },
                        {
                            id: "mock-8",
                            brand: "Chevrolet",
                            model: "Onix",
                            year: 2023,
                            dailyRate: 90,
                            available: true,
                        }
                    ]
                    setCars(mockCars)
                } else {
                    setCars(availableCars)
                }
            } catch (err) {
                console.error('Error fetching cars:', err)
                setError('Erro ao carregar veículos disponíveis')
            } finally {
                setLoading(false)
            }
        }

        fetchCars()
    }, [user, router])

    const handleCarSelect = (car: Automobile) => {
        setSelectedCar(car)
        setShowConfirmationModal(true)
    }

    const handleConfirmationClose = () => {
        setShowConfirmationModal(false)
        setSelectedCar(null)
    }

    const handleRentalSuccess = () => {
        setShowConfirmationModal(false)
        setSelectedCar(null)
        alert("Solicitação de aluguel enviada com sucesso!")
        router.push('/')
    }

    const handleGoBack = () => {
        router.push('/')
    }

    const getImageUrl = (car: Automobile) => {
        const imageName = `${car.brand.toLowerCase()}-${car.model.toLowerCase().replace(/\s+/g, '-')}.jpeg`
        return `/${imageName}`
    }

    const getCarCategory = (car: Automobile) => {
        const rate = safeNumber(car.dailyRate)
        if (rate <= 100) return "economy"
        if (rate <= 150) return "standard"
        if (rate <= 200) return "premium"
        return "luxury"
    }

    const getCategoryName = (category: string) => {
        const categories: { [key: string]: string } = {
            "all": "Todos",
            "economy": "Econômico",
            "standard": "Padrão",
            "premium": "Premium",
            "luxury": "Luxo"
        }
        return categories[category] || category
    }

    const getCategoryBadgeColor = (category: string) => {
        const colors: { [key: string]: string } = {
            "economy": "bg-green-100 text-green-800",
            "standard": "bg-blue-100 text-blue-800",
            "premium": "bg-purple-100 text-purple-800",
            "luxury": "bg-yellow-100 text-yellow-800"
        }
        return colors[category] || "bg-gray-100 text-gray-800"
    }

    const filteredCars = selectedCategory === "all"
        ? cars
        : cars.filter(car => getCarCategory(car) === selectedCategory)

    const categories = ["all", "economy", "standard", "premium", "luxury"]

    if (!user || user.userType !== 'cliente') {
        return null // Redirecionamento acontece no useEffect
    }

    if (loading) {
        return (
            <div className="min-h-screen relative flex items-center justify-center">
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

                {/* Conteúdo de loading */}
                <div className="relative z-20 text-center">
                    <div className="w-12 h-12 border-4 border-white/30 border-t-white rounded-full animate-spin mx-auto mb-4"></div>
                    <div className="text-xl text-white drop-shadow-lg">Carregando veículos disponíveis...</div>
                </div>
            </div>
        )
    }

    return (
        <>
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
                    {/* Header */}
                    <div className="bg-white/10 backdrop-blur-md border-b border-white/20">
                        <div className="container mx-auto px-4 py-6">
                            <div className="flex items-center justify-between">
                                <div className="flex items-center gap-4">
                                    <button
                                        onClick={handleGoBack}
                                        className="text-white hover:bg-white/10 p-2 rounded-full transition-colors"
                                    >
                                        <ArrowLeft className="w-6 h-6" />
                                    </button>
                                    <div>
                                        <h1 className="text-3xl font-bold text-white mb-2 drop-shadow-lg">Escolha Seu Veículo</h1>
                                        <p className="text-white/90 drop-shadow-md">Selecione o carro perfeito para sua viagem</p>
                                    </div>
                                </div>
                                <div className="text-right">
                                    <p className="text-white/90 drop-shadow-md">Olá, <span className="font-bold">{user.name}</span></p>
                                </div>
                            </div>
                        </div>
                    </div>

                    {error && (
                        <div className="bg-red-500/20 backdrop-blur-sm border-b border-red-500/30 p-4">
                            <p className="text-red-200 text-center">{error}</p>
                        </div>
                    )}

                    <main className="container mx-auto px-4 py-8">
                        {/* Category Filter */}
                        <div className="mb-8">
                            <div className="flex flex-wrap gap-3 justify-center">
                                {categories.map((category) => (
                                    <button
                                        key={category}
                                        onClick={() => setSelectedCategory(category)}
                                        className={`px-6 py-3 rounded-full text-sm font-medium transition-all backdrop-blur-sm ${
                                            selectedCategory === category
                                                ? 'bg-blue-600 text-white shadow-lg'
                                                : 'bg-white/10 text-white hover:bg-white/20 border border-white/20 shadow-sm'
                                        }`}
                                    >
                                        {getCategoryName(category)}
                                    </button>
                                ))}
                            </div>
                        </div>

                        {/* Cars Grid */}
                        {filteredCars.length === 0 ? (
                            <div className="text-center py-16">
                                <div className="bg-white/10 backdrop-blur-md rounded-xl border border-white/20 p-8 max-w-md mx-auto">
                                    <Car className="w-16 h-16 mx-auto mb-4 text-white/50" />
                                    <p className="text-xl text-white mb-2 font-medium">Nenhum veículo encontrado</p>
                                    <p className="text-sm text-white/80">Tente selecionar uma categoria diferente</p>
                                </div>
                            </div>
                        ) : (
                            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
                                {filteredCars.map((car) => (
                                    <div
                                        key={car.id}
                                        className="bg-white/10 backdrop-blur-md border border-white/20 rounded-xl shadow-lg hover:shadow-xl hover:bg-white/15 transition-all duration-200 overflow-hidden group cursor-pointer"
                                        onClick={() => handleCarSelect(car)}
                                    >
                                        {/* Car Image */}
                                        <div className="relative h-48 bg-gradient-to-br from-white/10 to-white/5 overflow-hidden">
                                            <img
                                                src={getImageUrl(car)}
                                                alt={`${car.brand} ${car.model}`}
                                                className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300"
                                                onError={(e) => {
                                                    const target = e.target as HTMLImageElement;
                                                    target.src = "data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iNDAwIiBoZWlnaHQ9IjMwMCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj4KICA8cmVjdCB3aWR0aD0iMTAwJSIgaGVpZ2h0PSIxMDAlIiBmaWxsPSIjZjNmNGY2Ii8+CiAgPHRleHQgeD0iNTAlIiB5PSI0NSUiIGZvbnQtZmFtaWx5PSJBcmlhbCwgc2Fucy1zZXJpZiIgZm9udC1zaXplPSIyNCIgZmlsbD0iIzk0YTNiOCIgdGV4dC1hbmNob3I9Im1pZGRsZSIgZHk9Ii4zZW0iPkNhcnJvPC90ZXh0PgogIDx0ZXh0IHg9IjUwJSIgeT0iNTUlIiBmb250LWZhbWlseT0iQXJpYWwsIHNhbnMtc2VyaWYiIGZvbnQtc2l6ZT0iMTgiIGZpbGw9IiM5NDk0YWMiIHRleHQtYW5jaG9yPSJtaWRkbGUiIGR5PSIuM2VtIj5JbWFnZW0gSW5kaXNwb27DrXZlbDwvdGV4dD4KPC9zdmc+";
                                                }}
                                            />

                                            {/* Category Badge */}
                                            <div className="absolute top-3 left-3">
                                                <span className={`px-2 py-1 rounded-full text-xs font-medium ${getCategoryBadgeColor(getCarCategory(car))}`}>
                                                    {getCategoryName(getCarCategory(car))}
                                                </span>
                                            </div>

                                        </div>

                                        {/* Car Info */}
                                        <div className="p-4">
                                            <div className="mb-3">
                                                <h3 className="text-lg font-bold text-white drop-shadow-md">
                                                    {car.brand} {car.model}
                                                </h3>
                                                <p className="text-sm text-white/80 drop-shadow-sm">{car.year}</p>
                                            </div>

                                            {/* Features */}
                                            <div className="flex items-center gap-4 mb-4 text-xs text-white/80">
                                                <div className="flex items-center gap-1">
                                                    <Users className="w-3 h-3" />
                                                    <span>5 pessoas</span>
                                                </div>
                                                <div className="flex items-center gap-1">
                                                    <Cog className="w-3 h-3" />
                                                    <span>Automático</span>
                                                </div>
                                                <div className="flex items-center gap-1">
                                                    <Fuel className="w-3 h-3" />
                                                    <span>Flex</span>
                                                </div>
                                            </div>

                                            {/* Price */}
                                            <div className="flex items-center justify-between">
                                                <div>
                                                    <div className="text-2xl font-bold text-white drop-shadow-lg">
                                                        {formatCurrency(safeNumber(car.dailyRate))}
                                                    </div>
                                                    <div className="text-xs text-white/70 drop-shadow-sm">por dia</div>
                                                </div>
                                                <button className="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg text-sm font-medium transition-colors shadow-lg">
                                                    Selecionar
                                                </button>
                                            </div>
                                        </div>
                                    </div>
                                ))}
                            </div>
                        )}

                        {/* Footer Info */}
                        <div className="mt-12 text-center">
                            <div className="bg-white/10 backdrop-blur-md rounded-xl border border-white/20 p-4 max-w-md mx-auto">
                                <div className="flex items-center justify-center gap-2 text-sm text-white/90">
                                    <Info className="w-4 h-4" />
                                    <span>Clique em um veículo para ver mais detalhes e confirmar o aluguel</span>
                                </div>
                            </div>
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

            {/* Confirmation Modal */}
            {showConfirmationModal && selectedCar && (
                <RentalConfirmationModal
                    selectedCar={selectedCar}
                    onClose={handleConfirmationClose}
                    onSuccess={handleRentalSuccess}
                />
            )}
        </>
    )
}

export default CarSelectionPage