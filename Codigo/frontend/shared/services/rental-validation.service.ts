// Salve este arquivo em: Codigo/frontend/shared/services/rental-validation.service.ts

import { ApiService } from './index';
import type { CreditLimitValidationResult } from '@/shared/types/credit-limit';
import { calculateDaysBetween, safeNumber } from '@/shared/utils/type-guards';

export class RentalValidationService {
    /**
     * Valida se um pedido de aluguel está dentro do limite de crédito do cliente
     */
    static async validateCreditLimit(
        customerId: string,
        automobileId: string,
        pickupDate: string,
        returnDate: string
    ): Promise<CreditLimitValidationResult> {
        try {
            // Buscar dados do cliente (incluindo creditLimit)
            const customer = await ApiService.customer.getCustomerById(customerId);
            
            // Buscar dados do automóvel (para pegar dailyRate)
            const automobile = await ApiService.automobile.getAutomobileById(automobileId);

            if (!customer || !automobile) {
                return {
                    isValid: false,
                    requestAmount: 0,
                    exceedsLimit: false,
                    message: 'Dados do cliente ou veículo não encontrados'
                };
            }

            // Calcular valor total do pedido
            const days = calculateDaysBetween(pickupDate, returnDate);
            const requestAmount = days * safeNumber(automobile.dailyRate);

            // Verificar se o cliente tem limite definido
            const customerCreditLimit = safeNumber(customer.creditLimit, 0);

            if (customerCreditLimit === 0) {
                return {
                    isValid: false,
                    customerCreditLimit: 0,
                    requestAmount,
                    exceedsLimit: false,
                    message: 'Cliente não possui limite de crédito definido. Entre em contato com o banco.'
                };
            }

            // Verificar se excede o limite
            const exceedsLimit = requestAmount > customerCreditLimit;

            return {
                isValid: !exceedsLimit,
                customerCreditLimit,
                requestAmount,
                exceedsLimit,
                message: exceedsLimit 
                    ? `O valor do pedido (R$ ${requestAmount.toFixed(2)}) excede o limite de crédito disponível (R$ ${customerCreditLimit.toFixed(2)}).`
                    : 'Pedido dentro do limite de crédito. Será enviado para análise.'
            };
        } catch (error) {
            console.error('Error validating credit limit:', error);
            return {
                isValid: false,
                requestAmount: 0,
                exceedsLimit: false,
                message: 'Erro ao validar limite de crédito'
            };
        }
    }

    /**
     * Verifica se um automóvel pertence ao banco
     * (Automóveis criados por agentes banco)
     */
    static async isAutomobileFromBank(automobileId: string): Promise<boolean> {
        try {
            const automobile = await ApiService.automobile.getAutomobileById(automobileId);
            
            if (!automobile) {
                return false;
            }

            // Verificar se o automóvel foi criado por um agente banco
            // Por enquanto, vamos assumir que todos os automóveis passam pela validação
            // Em produção, você deve adicionar um campo no backend para identificar isso
            
            return true; // Por padrão, considera que todos precisam de validação
        } catch (error) {
            console.error('Error checking automobile owner:', error);
            return false;
        }
    }
}