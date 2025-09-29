import {AutomobileSummary, CustomerSummary} from "@/shared/types/summary";

export interface RentalRequest {
    id?: string;
    pickupDate: string;
    returnDate: string;
    status?: RequestStatus;
    createdAt?: string;
    totalValue?: number;
    rentalDays?: number;
    observations?: string;
    customer?: CustomerSummary;
    automobile?: AutomobileSummary;
    processedByAgentId?: string;
    processedByAgentUsername?: string;
    processedAt?: string;
}

export interface RentalRequestCreateDTO {
    automobileId: string;
    pickupDate: string;
    returnDate: string;
    observations?: string;
}

export interface RentalRequestResponseDTO {
    id: string;
    pickupDate: string;
    returnDate: string;
    status: RequestStatus;
    statusDescription: string;
    createdAt: string;
    totalValue: number;
    rentalDays: number;
    observations?: string;
    customer: CustomerSummary;
    automobile: AutomobileSummary;
    processedByAgentId?: string;
    processedByAgentUsername?: string;
    processedAt?: string;
}

export interface RentalRequestUpdateDTO {
    pickupDate?: string;
    returnDate?: string;
    observations?: string;
}

export interface RentalRequestStatusUpdateDTO {
    status: RequestStatus;
    rejectionReason?: string;
}

export enum RequestStatus {
    PENDING = 'PENDING',
    UNDER_ANALYSIS = 'UNDER_ANALYSIS',
    APPROVED = 'APPROVED',
    REJECTED = 'REJECTED',
    CANCELLED = 'CANCELLED',
    ACTIVE = 'ACTIVE',
    COMPLETED = 'COMPLETED'
}