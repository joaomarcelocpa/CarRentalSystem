import { BaseApiService } from './base-api.service';
import { API_CONFIG } from '../config/api';
import type { Bank } from '../types/bank';
import type { CreditContract } from '../types/credit-contract';

export class BankService extends BaseApiService {
    async getAllBanks(): Promise<Bank[]> {
        return this.get<Bank[]>(API_CONFIG.ENDPOINTS.BANKS);
    }

    async getBankById(id: string): Promise<Bank | null> {
        try {
            return await this.get<Bank>(`${API_CONFIG.ENDPOINTS.BANKS}/${id}`);
        } catch (error) {
            if (error instanceof Error && error.message.includes('404')) {
                return null;
            }
            throw error;
        }
    }

    async createBank(bank: Bank): Promise<Bank> {
        return this.post<Bank>(API_CONFIG.ENDPOINTS.BANKS, bank);
    }

    async getBankByCode(bankCode: string): Promise<Bank | null> {
        try {
            const banks = await this.getAllBanks();
            return banks.find(bank => bank.bankCode === bankCode) || null;
        } catch (error) {
            return null;
        }
    }

    async grantCredit(bankId: string, contract: CreditContract): Promise<boolean> {
        return this.post<boolean>(`${API_CONFIG.ENDPOINTS.BANKS}/${bankId}/grant-credit`, contract);
    }
}