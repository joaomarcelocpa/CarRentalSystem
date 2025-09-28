export interface Automobile {
    id?: string;
    registration?: string;
    year: number;
    brand: string;
    model: string;
    licensePlate?: string;
    available: boolean;
    dailyRate: number;
    createdAt?: string;
}

export interface AutomobileCreate {
    licensePlate: string;
    brand: string;
    model: string;
    year: number;
    registration?: string;
    dailyRate: number;
}

export interface AutomobileResponse {
    id: string;
    licensePlate: string;
    brand: string;
    model: string;
    year: number;
    registration?: string;
    available: boolean;
    dailyRate: number;
    createdAt: string;
}