import {RentalRequest, RequestStatus} from "@/shared/types/rental-request";

export interface CustomerRef {
    id: string;
    name?: string;
    emailContact?: string;
}

export interface AutomobileRef {
    id: string;
    brand?: string;
    model?: string;
    year?: number;
    dailyRate?: number;
}

// Complete interfaces for full data
export interface CustomerComplete {
    id: string;
    name: string;
    emailContact: string;
    rg?: string;
    cpf?: string;
    address?: string;
    profession?: string;
    createdAt?: string;
}

export interface AutomobileComplete {
    id: string;
    brand: string;
    model: string;
    year: number;
    dailyRate: number;
    registration?: string;
    licensePlate?: string;
    available: boolean;
}

// Request interfaces for creating new records
export interface RentalRequestCreate {
    desiredStartDate: string;
    desiredEndDate: string;
    observations?: string;
    customer: CustomerRef;
    automobile: AutomobileRef;
}

// Response interfaces for API responses
export interface RentalRequestResponse {
    id: string;
    desiredStartDate: string;
    desiredEndDate: string;
    status: RequestStatus;
    creationDate: string;
    estimatedValue?: number;
    observations?: string;
    customer: CustomerComplete;
    automobile: AutomobileComplete;
}

// Utility type for partial updates
export type PartialRentalRequest = Partial<RentalRequest> & {
    id?: string;
}