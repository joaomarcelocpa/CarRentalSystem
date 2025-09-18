"use client"

import type React from "react"
import { useState, useEffect } from "react"
import type { Car } from "@/types/car"

const CarCarousel: React.FC = () => {
    const [currentSlide, setCurrentSlide] = useState(0)

    // Dados simulados de carros
    const cars: Car[] = [
        {
            id: "1",
            brand: "Toyota",
            model: "Corolla",
            year: 2023,
            price: 120,
            image: "/toyota-corolla-sedan-silver-modern-car.jpg",
            available: true,
        },
        {
            id: "2",
            brand: "Honda",
            model: "Civic",
            year: 2023,
            price: 130,
            image: "/honda-civic-sedan-blue-modern-car.jpg",
            available: true,
        },
        {
            id: "3",
            brand: "Volkswagen",
            model: "Jetta",
            year: 2022,
            price: 110,
            image: "/volkswagen-jetta-sedan-white-modern-car.jpg",
            available: true,
        },
        {
            id: "4",
            brand: "Hyundai",
            model: "Elantra",
            year: 2023,
            price: 115,
            image: "/hyundai-elantra-sedan-red-modern-car.jpg",
            available: true,
        },
    ]

    useEffect(() => {
        const timer = setInterval(() => {
            setCurrentSlide((prev) => (prev + 1) % cars.length)
        }, 5000)

        return () => clearInterval(timer)
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

    return (
        <div className="relative w-full max-w-4xl mx-auto bg-card rounded-xl shadow-lg overflow-hidden">
            <div className="relative h-96 overflow-hidden">
                {cars.map((car, index) => (
                    <div
                        key={car.id}
                        className={`absolute inset-0 transition-transform duration-500 ease-in-out ${
                            index === currentSlide ? "translate-x-0" : index < currentSlide ? "-translate-x-full" : "translate-x-full"
                        }`}
                    >
                        <img
                            src={car.image || "/placeholder.svg"}
                            alt={`${car.brand} ${car.model}`}
                            className="w-full h-full object-cover"
                        />
                        <div className="absolute inset-0 bg-gradient-to-t from-black/60 to-transparent" />
                        <div className="absolute bottom-6 left-6 text-white">
                            <h3 className="text-2xl font-bold">
                                {car.brand} {car.model}
                            </h3>
                            <p className="text-lg">
                                {car.year} • R$ {car.price}/dia
                            </p>
                        </div>
                    </div>
                ))}
            </div>

            {/* Navigation Arrows */}
            <button
                onClick={prevSlide}
                className="absolute left-4 top-1/2 -translate-y-1/2 bg-black/50 hover:bg-black/70 text-white p-2 rounded-full transition-colors"
            >
                <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 19l-7-7 7-7" />
                </svg>
            </button>
            <button
                onClick={nextSlide}
                className="absolute right-4 top-1/2 -translate-y-1/2 bg-black/50 hover:bg-black/70 text-white p-2 rounded-full transition-colors"
            >
                <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5l7 7-7 7" />
                </svg>
            </button>

            {/* Dots Indicator */}
            <div className="absolute bottom-4 left-1/2 -translate-x-1/2 flex space-x-2">
                {cars.map((_, index) => (
                    <button
                        key={index}
                        onClick={() => goToSlide(index)}
                        className={`w-3 h-3 rounded-full transition-colors ${index === currentSlide ? "bg-white" : "bg-white/50"}`}
                    />
                ))}
            </div>
        </div>
    )
}

export default CarCarousel
