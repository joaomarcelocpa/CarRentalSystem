export interface CustomerSummary {
    id: string;
    name?: string;
    emailContact?: string;
}

export interface AutomobileSummary {
    id: string;
    brand?: string;
    model?: string;
    year?: number;
    dailyRate?: number;
}

export interface RentalRequestSummary {
    id: string;
    desiredStartDate: string;
    desiredEndDate: string;
    status: string;
    estimatedValue?: number;
}