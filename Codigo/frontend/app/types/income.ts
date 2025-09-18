import {Customer} from "@/app/types/customer";

export interface Income {
    id?: string;
    value: number;
    type: IncomeType;
    startDate?: string;
    endDate?: string;
    customer?: Customer;
}

export enum IncomeType {
    SALARY = 'SALARY',
    FREELANCE = 'FREELANCE',
    BUSINESS = 'BUSINESS',
    OTHERS = 'OTHERS'
}