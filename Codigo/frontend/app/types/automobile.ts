export interface Automobile {
    id?: string;
    registration?: string;
    year: number;
    brand: string;
    model: string;
    licensePlate?: string;
    available: boolean;
    dailyRate: number;
}