"use client"

import type React from "react"
import { useState, useEffect } from "react"
import { ApiService } from "@/shared/services"
import type { Automobile } from "@/shared/types/automobile"
import { formatCurrency, safeNumber } from "@/shared/utils/type-guards"

const CarCarousel: React.FC = () => {
    const [currentSlide, setCurrentSlide] = useState(0)
    const [cars, setCars] = useState<Automobile[]>([])
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState<string | null>(null)

    useEffect(() => {
        const fetchCars = async () => {
            try {
                setLoading(true)
                const availableCars = await ApiService.automobile.getAvailableAutomobiles()

                if (availableCars.length === 0) {
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
                    ]
                    setCars(mockCars)
                } else {
                    setCars(availableCars)
                }
            } catch (err) {
                console.error('Error fetching cars:', err)
                setError('Erro ao carregar veículos')

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
                ]
                setCars(mockCars)
            } finally {
                setLoading(false)
            }
        }

        fetchCars()
    }, [])

    useEffect(() => {
        if (cars.length > 0) {
            const timer = setInterval(() => {
                setCurrentSlide((prev) => (prev + 1) % cars.length)
            }, 5000)

            return () => clearInterval(timer)
        }
    }, [cars.length])

    const nextSlide = () => {
        setCurrentSlide((prev) => (prev + 1) % cars.length)
    }

    const prevSlide = () => {
        setCurrentSlide((prev) => (prev - 1 + cars.length) % cars.length)
    }

    const goToSlide = (index: number) => {
        setCurrentSlide(index)
    }

    const getImageUrl = (car: Automobile) => {
        const imageName = `${car.brand.toLowerCase()}-${car.model.toLowerCase().replace(/\s+/g, '-')}.jpeg`
        return `/${imageName}`
    }

    if (loading) {
        return (
            <div className="relative w-full max-w-4xl mx-auto bg-white/10 backdrop-blur-md rounded-xl shadow-lg overflow-hidden border border-white/20">
                <div className="h-96 flex items-center justify-center">
                    <div className="text-white text-xl">Carregando veículos...</div>
                </div>
            </div>
        )
    }

    if (error && cars.length === 0) {
        return (
            <div className="relative w-full max-w-4xl mx-auto bg-white/10 backdrop-blur-md rounded-xl shadow-lg overflow-hidden border border-white/20">
                <div className="h-96 flex items-center justify-center">
                    <div className="text-red-300 text-xl">Erro ao carregar veículos</div>
                </div>
            </div>
        )
    }

    return (
        <div className="relative w-full max-w-4xl mx-auto bg-white/10 backdrop-blur-md rounded-xl shadow-lg overflow-hidden border border-white/20">
            <div className="relative h-96 overflow-hidden">
                {cars.map((car, index) => (
                    <div
                        key={car.id}
                        className={`absolute inset-0 transition-transform duration-500 ease-in-out ${
                            index === currentSlide ? "translate-x-0" : index < currentSlide ? "-translate-x-full" : "translate-x-full"
                        }`}
                    >
                        <img
                            src={getImageUrl(car)}
                            alt={`${car.brand} ${car.model}`}
                            className="w-full h-full object-cover"
                            onError={(e) => {
                                // Fallback to a simple colored placeholder
                                const target = e.target as HTMLImageElement;
                                target.src = "data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iNDAwIiBoZWlnaHQ9IjMwMCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj4KICA8cmVjdCB3aWR0aD0iMTAwJSIgaGVpZ2h0PSIxMDAlIiBmaWxsPSIjZjNmNGY2Ii8+CiAgPHRleHQgeD0iNTAlIiB5PSI1MCUiIGZvbnQtZmFtaWx5PSJBcmlhbCwgc2Fucy1zZXJpZiIgZm9udC1zaXplPSIyNCIgZmlsbD0iIzk0YTNiOCIgdGV4dC1hbmNob3I9Im1pZGRsZSIgZHk9Ii4zZW0iPkNhcnJvIG7Do28gZW5jb250cmFkbzwvdGV4dD4KICA8dGV4dCB4PSI1MCUiIHk9IjUwJSIgZm9udC1mYW1pbHk9IkFyaWFsLCBzYW5zLXNlcmlmIiBmb250LXNpemU9IjE4IiBmaWxsPSIjOWM5Y2FjIiB0ZXh0LWFuY2hvcj0ibWlkZGxlIiBkeT0iMWVtIj5pbWFnZW0gaW5kaXNwb27DrXZlbDwvdGV4dD4KPC9zdmc+";
                            }}
                        />
                        <div className="absolute inset-0 bg-gradient-to-t from-black/70 to-transparent" />
                        <div className="absolute bottom-6 left-6 text-white">
                            <h3 className="text-2xl font-bold drop-shadow-lg">
                                {car.brand} {car.model}
                            </h3>
                            <p className="text-lg drop-shadow-md">
                                {car.year} • {formatCurrency(safeNumber(car.dailyRate))}/dia
                            </p>
                            {!car.available && (
                                <span className="inline-block bg-red-600 text-white px-2 py-1 rounded text-sm mt-1">
                                    Indisponível
                                </span>
                            )}
                        </div>
                    </div>
                ))}
            </div>

            {/* Navigation Arrows */}
            {cars.length > 1 && (
                <>
                    <button
                        onClick={prevSlide}
                        className="absolute left-4 top-1/2 -translate-y-1/2 bg-black/50 hover:bg-black/70 text-white p-2 rounded-full transition-colors backdrop-blur-sm"
                    >
                        <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 19l-7-7 7-7" />
                        </svg>
                    </button>
                    <button
                        onClick={nextSlide}
                        className="absolute right-4 top-1/2 -translate-y-1/2 bg-black/50 hover:bg-black/70 text-white p-2 rounded-full transition-colors backdrop-blur-sm"
                    >
                        <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5l7 7-7 7" />
                        </svg>
                    </button>
                </>
            )}

            {/* Dots Indicator */}
            {cars.length > 1 && (
                <div className="absolute bottom-4 left-1/2 -translate-x-1/2 flex space-x-2">
                    {cars.map((_, index) => (
                        <button
                            key={index}
                            onClick={() => goToSlide(index)}
                            className={`w-3 h-3 rounded-full transition-colors ${
                                index === currentSlide ? "bg-white" : "bg-white/50"
                            }`}
                        />
                    ))}
                </div>
            )}

            {/* Car count display */}
            <div className="absolute top-4 right-4 bg-black/50 backdrop-blur-sm text-white px-3 py-1 rounded-full text-sm">
                {cars.length} {cars.length === 1 ? 'veículo' : 'veículos'} disponível{cars.length !== 1 ? 'eis' : ''}
            </div>
        </div>
    )
}

export default CarCarousel