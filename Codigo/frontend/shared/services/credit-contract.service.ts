import { BaseApiService } from './base-api.service';
import type {
    CreditContractCreateDTO,
    CreditContractUpdateDTO,
    CreditContractResponseDTO
} from '@/shared/types/credit-contract';

export class CreditContractService extends BaseApiService {
    async createOrUpdateCreditLimit(data: CreditContractCreateDTO): Promise<CreditContractResponseDTO> {
        return this.post<CreditContractResponseDTO>('/credit-contracts', data);
    }

    async getAllCreditContracts(): Promise<CreditContractResponseDTO[]> {
        return this.get<CreditContractResponseDTO[]>('/credit-contracts');
    }

    async getCreditContractById(id: string): Promise<CreditContractResponseDTO> {
        return this.get<CreditContractResponseDTO>(`/credit-contracts/${id}`);
    }

    async updateCreditContract(id: string, data: CreditContractUpdateDTO): Promise<CreditContractResponseDTO> {
        return this.put<CreditContractResponseDTO>(`/credit-contracts/${id}`, data);
    }

    async deleteCreditContract(id: string): Promise<void> {
        return this.delete(`/credit-contracts/${id}`);
    }

    async checkCreditLimit(customerUsername: string, amount: number): Promise<{ hasAvailableCredit: boolean }> {
        return this.get<{ hasAvailableCredit: boolean }>(
            `/credit-contracts/check-limit?customerUsername=${customerUsername}&amount=${amount}`
        );
    }
}