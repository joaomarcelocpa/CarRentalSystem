// Codigo/frontend/shared/types/customer.ts
import {RentalRequestSummary} from "@/shared/types/summary";

export interface Customer {
    id?: string;
    name: string;
    emailContact: string;
    rg?: string;
    cpf?: string;
    address?: string;
    profession?: string;
    creditLimit?: number;
    createdAt?: string;
    rentalRequests?: RentalRequestSummary[];
}

export interface CustomerCreateDTO {
    name: string;
    emailContact: string;
    rg?: string;
    cpf?: string;
    address?: string;
    profession?: string;
}

export interface CustomerResponseDTO {
    id: string;
    name: string;
    emailContact: string;
    rg?: string;
    cpf?: string;
    address?: string;
    profession?: string;
    creditLimit?: number;
    createdAt: string;
    rentalRequests?: RentalRequestSummary[];
}