import {RentalRequest} from "@/app/types/rental-request";

export interface RentalContract {
    id?: string;
    startDate: string;
    endDate: string;
    value: number;
    signingDate?: string;
    terms?: string;
    rentalRequest?: RentalRequest;
}
