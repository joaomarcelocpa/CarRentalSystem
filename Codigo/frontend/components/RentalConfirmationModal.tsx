/* eslint-disable @typescript-eslint/no-explicit-any */
/* eslint-disable @next/next/no-img-element */
// Codigo/frontend/components/RentalConfirmationModal.tsx
"use client";

import type React from "react";
import { useState, useEffect } from "react";
import { useAuth } from "@/shared/contexts/AuthContext";
import { ApiService } from "@/shared/services";
import { RentalValidationService } from "@/shared/services/rental-validation.service";
import type { Automobile } from "@/shared/types/automobile";
import type { RentalRequestCreateDTO } from "@/shared/types/rental-request";
import type { CreditLimitValidationResult } from "@/shared/types/credit-limit";
import {
  Calendar,
  X,
  CheckCircle,
  Fuel,
  Users,
  Cog,
  Shield,
  AlertCircle,
  AlertTriangle,
} from "lucide-react";
import { formatCurrency, safeNumber } from "@/shared/utils/type-guards";

interface RentalConfirmationModalProps {
  selectedCar: Automobile;
  onClose: () => void;
  onSuccess?: () => void;
}

const RentalConfirmationModal: React.FC<RentalConfirmationModalProps> = ({
  selectedCar,
  onClose,
  onSuccess,
}) => {
  const { user } = useAuth();
  const [pickupDate, setPickupDate] = useState("");
  const [returnDate, setReturnDate] = useState("");
  const [observations, setObservations] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState("");
  const [estimatedValue, setEstimatedValue] = useState<number>(0);
  const [rentalDays, setRentalDays] = useState<number>(0);
  const [creditValidation, setCreditValidation] =
    useState<CreditLimitValidationResult | null>(null);
  const [isValidating, setIsValidating] = useState(false);

  useEffect(() => {
    if (pickupDate && returnDate) {
      const start = new Date(pickupDate);
      const end = new Date(returnDate);
      const diffTime = Math.abs(end.getTime() - start.getTime());
      const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
      setRentalDays(diffDays);
      setEstimatedValue(diffDays * safeNumber(selectedCar.dailyRate));

      // Validar limite de crédito quando as datas mudarem
      if (user?.id) {
        validateCreditLimit(user.id, selectedCar.id!, pickupDate, returnDate);
      }
    } else {
      setEstimatedValue(0);
      setRentalDays(0);
      setCreditValidation(null);
    }
  }, [pickupDate, returnDate, selectedCar.dailyRate, selectedCar.id, user]);

  const validateCreditLimit = async (
    customerId: string,
    automobileId: string,
    pickup: string,
    returnD: string
  ) => {
    setIsValidating(true);
    try {
      const validation = await RentalValidationService.validateCreditLimit(
        customerId,
        automobileId,
        pickup,
        returnD
      );
      setCreditValidation(validation);
    } catch (error) {
      console.error("Error validating credit limit:", error);
    } finally {
      setIsValidating(false);
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!user) {
      setError("Usuário não autenticado");
      return;
    }

    if (!pickupDate || !returnDate) {
      setError("Todos os campos obrigatórios devem ser preenchidos");
      return;
    }

    const start = new Date(pickupDate);
    const end = new Date(returnDate);

    if (end <= start) {
      setError("A data de devolução deve ser posterior à data de retirada");
      return;
    }

    const today = new Date();
    today.setHours(0, 0, 0, 0);
    start.setHours(0, 0, 0, 0);

    if (start < today) {
      setError("A data de retirada não pode ser anterior a hoje");
      return;
    }

    // Verificar validação de crédito
    if (creditValidation && !creditValidation.isValid) {
      setError(creditValidation.message || "Pedido não pode ser processado");
      return;
    }

    setIsLoading(true);
    setError("");

    try {
      const requestData: RentalRequestCreateDTO = {
        automobileId: selectedCar.id!,
        pickupDate: pickupDate,
        returnDate: returnDate,
        observations: observations,
      };

      await ApiService.rentalRequest.createRentalRequest(requestData);

      // Mensagem diferente dependendo da validação
      if (creditValidation?.exceedsLimit) {
        alert(
          "Seu pedido foi criado, mas foi automaticamente rejeitado pois excede seu limite de crédito."
        );
      } else {
        alert(
          "Solicitação de aluguel enviada com sucesso! Aguarde análise do agente."
        );
      }

      if (onSuccess) {
        onSuccess();
      }
    } catch (error: any) {
      console.error("Error creating rental request:", error);
      setError(
        error?.message ||
          "Erro ao criar solicitação de aluguel. Tente novamente."
      );
    } finally {
      setIsLoading(false);
    }
  };

  const getImageUrl = (car: Automobile) => {
    const imageName = `${car.brand.toLowerCase()}-${car.model
      .toLowerCase()
      .replace(/\s+/g, "-")}.jpeg`;
    return `/${imageName}`;
  };

  const getCarCategory = (car: Automobile) => {
    const rate = safeNumber(car.dailyRate);
    if (rate <= 100) return "Econômico";
    if (rate <= 150) return "Padrão";
    if (rate <= 200) return "Premium";
    return "Luxo";
  };

  const minPickupDate = new Date().toISOString().split("T")[0];
  const minReturnDate = pickupDate
    ? new Date(new Date(pickupDate).getTime() + 86400000)
        .toISOString()
        .split("T")[0]
    : minPickupDate;

  return (
    <div className="fixed inset-0 bg-black/80 bg-opacity-60 flex items-center justify-center p-2 z-30">
      <div className="bg-white rounded-2xl shadow-2xl w-full max-w-4xl max-h-[95vh] overflow-y-auto">
        {/* Header */}
        <div className="bg-gradient-to-r from-blue-600 to-blue-700 text-white p-3">
          <div className="flex justify-between items-center">
            <div>
              <h2 className="text-xl font-bold ml-2">
                Confirmar Aluguel - {selectedCar.brand} {selectedCar.model} (
                {selectedCar.year})
              </h2>
            </div>
            <button
              onClick={onClose}
              className="text-white hover:text-red-600 hover:bg-opacity-10 p-2 rounded-full transition-colors"
            >
              <X className="w-6 h-6" />
            </button>
          </div>
        </div>

        <div className="flex flex-col lg:flex-row">
          {/* Car Info - Left Side */}
          <div className="lg:w-2/5 p-6 border-r bg-gray-50">
            {/* Car Image */}
            <div className="relative h-40 bg-gradient-to-br from-gray-100 to-gray-200 rounded-xl overflow-hidden mb-4">
              <img
                src={getImageUrl(selectedCar)}
                alt={`${selectedCar.brand} ${selectedCar.model}`}
                className="w-full h-full object-cover"
                onError={(e) => {
                  const target = e.target as HTMLImageElement;
                  target.src =
                    "data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iNDAwIiBoZWlnaHQ9IjMwMCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj4KICA8cmVjdCB3aWR0aD0iMTAwJSIgaGVpZ2h0PSIxMDAlIiBmaWxsPSIjZjNmNGY2Ii8+CiAgPHRleHQgeD0iNTAlIiB5PSI0NSUiIGZvbnQtZmFtaWx5PSJBcmlhbCwgc2Fucy1zZXJpZiIgZm9udC1zaXplPSIyNCIgZmlsbD0iIzk0YTNiOCIgdGV4dC1hbmNob3I9Im1pZGRsZSIgZHk9Ii4zZW0iPkNhcnJvPC90ZXh0Pgo8L3N2Zz4=";
                }}
              />
              <div className="absolute top-3 left-3 bg-blue-600 text-white px-2 py-1 rounded-full text-xs font-medium">
                {getCarCategory(selectedCar)}
              </div>
            </div>

            {/* Features Grid */}
            <div className="grid grid-cols-2 gap-3 mb-4">
              <div className="bg-white p-3 rounded-lg border text-center">
                <Users className="w-5 h-5 text-blue-600 mx-auto mb-1" />
                <span className="text-sm font-medium text-black">
                  5 pessoas
                </span>
              </div>
              <div className="bg-white p-3 rounded-lg border text-center">
                <Cog className="w-5 h-5 text-blue-600 mx-auto mb-1" />
                <span className="text-sm font-medium text-black">
                  Automático
                </span>
              </div>
              <div className="bg-white p-3 rounded-lg border text-center">
                <Fuel className="w-5 h-5 text-blue-600 mx-auto mb-1" />
                <span className="text-sm font-medium text-black">Flex</span>
              </div>
              <div className="bg-white p-3 rounded-lg border text-center">
                <Shield className="w-5 h-5 text-blue-600 mx-auto mb-1" />
                <span className="text-sm font-medium text-black">Seguro</span>
              </div>
            </div>

            {/* Price per day */}
            <div className="text-center mb-4">
              <div className="text-2xl font-bold text-blue-600">
                {formatCurrency(safeNumber(selectedCar.dailyRate))}
              </div>
              <div className="text-sm text-gray-500">por dia</div>
            </div>
          </div>

          {/* Form - Right Side */}
          <div className="lg:w-3/5 p-6">
            {error && (
              <div className="mb-4 p-3 bg-red-50 border border-red-200 rounded-lg flex items-start gap-2">
                <AlertCircle className="w-5 h-5 text-red-600 flex-shrink-0 mt-0.5" />
                <p className="text-red-600 text-sm">{error}</p>
              </div>
            )}

            <form onSubmit={handleSubmit} className="space-y-4">
              {/* Dates */}
              <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    <Calendar className="w-4 h-4 inline mr-1" />
                    Data de Retirada *
                  </label>
                  <input
                    type="date"
                    value={pickupDate}
                    onChange={(e) => setPickupDate(e.target.value)}
                    min={minPickupDate}
                    className="w-full px-3 py-2 text-black border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent text-sm"
                    required
                  />
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    <Calendar className="w-4 h-4 inline mr-1" />
                    Data de Devolução *
                  </label>
                  <input
                    type="date"
                    value={returnDate}
                    onChange={(e) => setReturnDate(e.target.value)}
                    min={minReturnDate}
                    className="w-full px-3 py-2 text-black border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent text-sm"
                    required
                  />
                </div>
              </div>

              {/* Validação de Crédito */}
              {isValidating && (
                <div className="bg-gray-50 border border-gray-200 rounded-lg p-4">
                  <div className="flex items-center gap-2">
                    <div className="w-4 h-4 border-2 border-blue-600 border-t-transparent rounded-full animate-spin"></div>
                    <span className="text-sm text-gray-600">
                      Validando limite de crédito...
                    </span>
                  </div>
                </div>
              )}

              {creditValidation && !isValidating && (
                <div
                  className={`border rounded-lg p-4 ${
                    creditValidation.exceedsLimit
                      ? "bg-red-50 border-red-300"
                      : creditValidation.isValid
                      ? "bg-green-50 border-green-300"
                      : "bg-yellow-50 border-yellow-300"
                  }`}
                >
                  <div className="flex items-start gap-3">
                    {creditValidation.exceedsLimit ? (
                      <AlertTriangle className="w-5 h-5 text-red-600 flex-shrink-0 mt-0.5" />
                    ) : creditValidation.isValid ? (
                      <CheckCircle className="w-5 h-5 text-green-600 flex-shrink-0 mt-0.5" />
                    ) : (
                      <AlertCircle className="w-5 h-5 text-yellow-600 flex-shrink-0 mt-0.5" />
                    )}
                    <div className="flex-1">
                      <p
                        className={`text-sm font-medium mb-2 ${
                          creditValidation.exceedsLimit
                            ? "text-red-800"
                            : creditValidation.isValid
                            ? "text-green-800"
                            : "text-yellow-800"
                        }`}
                      >
                        {creditValidation.message}
                      </p>
                      {creditValidation.customerCreditLimit !== undefined && (
                        <div className="text-xs space-y-1">
                          <p className="text-gray-700">
                            <strong>Seu limite:</strong>{" "}
                            {formatCurrency(
                              creditValidation.customerCreditLimit
                            )}
                          </p>
                          <p className="text-gray-700">
                            <strong>Valor do pedido:</strong>{" "}
                            {formatCurrency(creditValidation.requestAmount)}
                          </p>
                        </div>
                      )}
                      {creditValidation.exceedsLimit && (
                        <p className="text-xs text-red-600 mt-2">
                          ⚠️ Seu pedido será criado, mas será automaticamente
                          rejeitado.
                        </p>
                      )}
                    </div>
                  </div>
                </div>
              )}

              {/* Price Summary */}
              {estimatedValue > 0 && (
                <div className="bg-blue-50 border border-blue-200 rounded-lg p-4">
                  <h4 className="text-lg font-semibold text-blue-800 mb-3">
                    Resumo do Pedido
                  </h4>
                  <div className="space-y-2">
                    <div className="flex justify-between text-sm">
                      <span className="text-blue-700">Diária do veículo</span>
                      <span className="font-medium">
                        {formatCurrency(safeNumber(selectedCar.dailyRate))}
                      </span>
                    </div>
                    <div className="flex justify-between text-sm">
                      <span className="text-blue-700">Quantidade de dias</span>
                      <span className="font-medium">
                        {rentalDays} {rentalDays === 1 ? "dia" : "dias"}
                      </span>
                    </div>
                    <div className="border-t border-blue-200 pt-2">
                      <div className="flex justify-between items-center">
                        <span className="text-base font-semibold text-blue-800">
                          Total Estimado
                        </span>
                        <span className="text-xl font-bold text-blue-800">
                          {formatCurrency(estimatedValue)}
                        </span>
                      </div>
                    </div>
                  </div>
                </div>
              )}

              {/* Observations */}
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Observações Especiais
                </label>
                <textarea
                  value={observations}
                  onChange={(e) => setObservations(e.target.value)}
                  rows={3}
                  className="w-full px-3 py-2 text-black border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent text-sm"
                  placeholder="Adicione observações ou solicitações especiais..."
                />
              </div>

              {/* Action Buttons */}
              <div className="flex gap-3 pt-4">
                <button
                  type="button"
                  onClick={onClose}
                  className="flex-1 px-4 py-2 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50 transition-colors text-sm font-medium"
                >
                  Cancelar
                </button>
                <button
                  type="submit"
                  disabled={
                    isLoading || !pickupDate || !returnDate || isValidating
                  }
                  className="flex-1 px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors text-sm font-medium"
                >
                  {isLoading ? (
                    <div className="flex items-center justify-center gap-2">
                      <div className="w-4 h-4 border-2 border-white border-opacity-30 border-t-white rounded-full animate-spin" />
                      Processando...
                    </div>
                  ) : (
                    <div className="flex items-center justify-center gap-2">
                      <CheckCircle className="w-4 h-4" />
                      {creditValidation?.exceedsLimit
                        ? "Criar Pedido (Será Rejeitado)"
                        : "Confirmar Aluguel"}
                    </div>
                  )}
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
  );
};

export default RentalConfirmationModal;
