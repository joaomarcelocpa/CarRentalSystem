export interface CreditLimitUpdateDTO {
    creditLimit: number;
}

export interface CustomerWithCreditLimit {
    id: string;
    name: string;
    emailContact: string;
    creditLimit?: number;
    createdAt: string;
    rg?: string;
    cpf?: string;
    address?: string;
    profession?: string;
}

export interface CreditLimitValidationResult {
    isValid: boolean;
    customerCreditLimit?: number;
    requestAmount: number;
    exceedsLimit: boolean;
    message?: string;
}