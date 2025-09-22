import {RentalRequest} from "@/shared/types/rental-request";

export interface CreditContract {
    id?: string;
    value: number;
    interestRate: number;
    term: number;
    grantDate?: string;
    status?: string;
    rentalRequest?: RentalRequest;
}