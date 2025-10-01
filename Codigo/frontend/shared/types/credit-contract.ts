import { CustomerSummary } from "@/shared/types/summary";

export interface CreditContract {
    id: string;
    customer: CustomerSummary;
    bankAgentId: string;
    bankAgentUsername: string;
    creditLimit: number;
    availableLimit: number;
    usedLimit: number;
    usagePercentage: number;
    createdAt: string;
    updatedAt: string;
    status: string;
}

export interface CreditContractCreateDTO {
    customerId: string;
    creditLimit: number;
}

export interface CreditContractUpdateDTO {
    creditLimit: number;
    status?: string;
}

export interface CreditContractResponseDTO extends CreditContract {}