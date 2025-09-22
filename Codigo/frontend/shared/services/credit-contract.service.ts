import { BaseApiService } from './base-api.service';
import { API_CONFIG } from '@/shared/config/api';
import type { CreditContract } from '@/shared/types/credit-contract';

export class CreditContractService extends BaseApiService {
    async createCreditContract(contract: CreditContract): Promise<CreditContract> {
        return this.post<CreditContract>(API_CONFIG.ENDPOINTS.CREDIT_CONTRACTS, contract);
    }
}