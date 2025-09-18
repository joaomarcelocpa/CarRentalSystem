import {AutomobileSummary, CustomerSummary} from "@/app/types/summary";

export interface RentalRequest {
    id?: string;
    desiredStartDate: string;
    desiredEndDate: string;
    status?: RequestStatus;
    creationDate?: string;
    estimatedValue?: number;
    observations?: string;
    customer?: CustomerSummary;
    automobile?: AutomobileSummary;
}

export interface RentalRequestCreateDTO {
    desiredStartDate: string;
    desiredEndDate: string;
    observations?: string;
    customerId: string;
    automobileId: string;
}

export enum RequestStatus {
    CREATED = 'CREATED',
    UNDER_ANALYSIS = 'UNDER_ANALYSIS',
    APPROVED = 'APPROVED',
    REJECTED = 'REJECTED',
    CANCELLED = 'CANCELLED',
    EXECUTED = 'EXECUTED'
}