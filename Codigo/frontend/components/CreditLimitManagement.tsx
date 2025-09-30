/* eslint-disable @typescript-eslint/no-explicit-any */
"use client";

import React, { useState, useEffect } from "react";
import { ApiService } from "@/shared/services";
import type { CustomerResponseDTO } from "@/shared/types/customer";
import {
  DollarSign,
  Users,
  Save,
  AlertCircle,
  CheckCircle,
  Edit2,
  X,
} from "lucide-react";
import { formatCurrency } from "@/shared/utils/type-guards";

interface CreditLimitManagementProps {
  className?: string;
}

const CreditLimitManagement: React.FC<CreditLimitManagementProps> = ({
  className = "",
}) => {
  const [customers, setCustomers] = useState<CustomerResponseDTO[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [editingCustomerId, setEditingCustomerId] = useState<string | null>(
    null
  );
  const [editingValue, setEditingValue] = useState<string>("");
  const [savingCustomerId, setSavingCustomerId] = useState<string | null>(null);
  const [successMessage, setSuccessMessage] = useState<string | null>(null);
  const [searchTerm, setSearchTerm] = useState("");

  useEffect(() => {
    loadCustomers();
  }, []);

  const loadCustomers = async () => {
    try {
      setLoading(true);
      setError(null);
      const allCustomers = await ApiService.customer.getAllCustomers();

      // Ordenar por nome
      allCustomers.sort((a, b) => (a.name ?? "").localeCompare(b.name ?? ""));

      setCustomers(allCustomers);
    } catch (err) {
      console.error("Error loading customers:", err);
      setError("Erro ao carregar clientes. Verifique sua permissão.");
    } finally {
      setLoading(false);
    }
  };

  const handleEditClick = (customer: CustomerResponseDTO) => {
    setEditingCustomerId(customer.id);
    setEditingValue((customer.creditLimit || 0).toString());
    setSuccessMessage(null);
  };

  const handleCancelEdit = () => {
    setEditingCustomerId(null);
    setEditingValue("");
  };

  const handleSave = async (customerId: string) => {
    try {
      setSavingCustomerId(customerId);
      setError(null);

      const creditLimit = parseFloat(editingValue);

      if (isNaN(creditLimit) || creditLimit < 0) {
        setError("Valor inválido. O limite deve ser um número positivo.");
        return;
      }

      const customerToUpdate = customers.find((c) => c.id === customerId);
      if (!customerToUpdate) {
        setError("Cliente não encontrado.");
        return;
      }

      const updatedData: any = {
        name: customerToUpdate.name,
        emailContact: customerToUpdate.emailContact,
        creditLimit: creditLimit,
      };
      if (customerToUpdate.rg) updatedData.rg = customerToUpdate.rg;
      if (customerToUpdate.cpf) updatedData.cpf = customerToUpdate.cpf;
      if (customerToUpdate.address)
        updatedData.address = customerToUpdate.address;
      if (customerToUpdate.profession)
        updatedData.profession = customerToUpdate.profession;

      // Remove undefined/null fields
      Object.keys(updatedData).forEach(
        (key) => updatedData[key] == null && delete updatedData[key]
      );

      await ApiService.customer.updateCreditLimit(customerId, creditLimit);

      setCustomers((prev) =>
        prev.map((c) => (c.id === customerId ? { ...c, creditLimit } : c))
      );

      setEditingCustomerId(null);
      setEditingValue("");
      setSuccessMessage(`Limite de crédito atualizado com sucesso!`);

      setTimeout(() => setSuccessMessage(null), 3000);
    } catch (err) {
      console.error("Error updating credit limit:", err);
      setError("Erro ao atualizar limite de crédito.");
    } finally {
      setSavingCustomerId(null);
    }
  };

  const filteredCustomers = customers.filter(
    (customer) =>
      (customer.name ?? "").toLowerCase().includes(searchTerm.toLowerCase()) ||
      (customer.emailContact ?? "")
        .toLowerCase()
        .includes(searchTerm.toLowerCase()) ||
      customer.cpf?.includes(searchTerm)
  );

  const totalCustomers = customers.length;
  const customersWithLimit = customers.filter(
    (c) => (c.creditLimit || 0) > 0
  ).length;
  const totalCreditLimitGranted = customers.reduce(
    (sum, c) => sum + (c.creditLimit || 0),
    0
  );

  if (loading) {
    return (
      <div className={`${className} flex items-center justify-center py-12`}>
        <div className="text-white/80 text-lg">Carregando clientes...</div>
      </div>
    );
  }

  return (
    <div className={`${className} space-y-6`}>
      {/* Header com Estatísticas */}
      <div className="bg-white/10 backdrop-blur-md rounded-xl border border-white/20 p-6">
        <div className="flex items-center justify-between mb-6">
          <div className="flex items-center gap-3">
            <div className="w-12 h-12 bg-blue-600 rounded-full flex items-center justify-center">
              <DollarSign className="w-6 h-6 text-white" />
            </div>
            <div>
              <h2 className="text-2xl font-bold text-white">
                Gestão de Limites de Crédito
              </h2>
              <p className="text-white/80">
                Defina limites de crédito para clientes
              </p>
            </div>
          </div>
        </div>

        {/* Estatísticas */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <div className="bg-white/5 backdrop-blur-sm rounded-lg p-4 border border-white/10">
            <div className="flex items-center gap-3">
              <Users className="w-8 h-8 text-blue-400" />
              <div>
                <p className="text-white/60 text-sm">Total de Clientes</p>
                <p className="text-2xl font-bold text-white">
                  {totalCustomers}
                </p>
              </div>
            </div>
          </div>

          <div className="bg-white/5 backdrop-blur-sm rounded-lg p-4 border border-white/10">
            <div className="flex items-center gap-3">
              <CheckCircle className="w-8 h-8 text-green-400" />
              <div>
                <p className="text-white/60 text-sm">Com Limite Definido</p>
                <p className="text-2xl font-bold text-white">
                  {customersWithLimit}
                </p>
              </div>
            </div>
          </div>

          <div className="bg-white/5 backdrop-blur-sm rounded-lg p-4 border border-white/10">
            <div className="flex items-center gap-3">
              <DollarSign className="w-8 h-8 text-yellow-400" />
              <div>
                <p className="text-white/60 text-sm">Crédito Total Concedido</p>
                <p className="text-2xl font-bold text-white">
                  {formatCurrency(totalCreditLimitGranted)}
                </p>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Mensagens */}
      {error && (
        <div className="bg-red-500/20 border border-red-500/50 rounded-lg p-4 flex items-center gap-2">
          <AlertCircle className="w-5 h-5 text-red-400" />
          <span className="text-red-200">{error}</span>
        </div>
      )}

      {successMessage && (
        <div className="bg-green-500/20 border border-green-500/50 rounded-lg p-4 flex items-center gap-2">
          <CheckCircle className="w-5 h-5 text-green-400" />
          <span className="text-green-200">{successMessage}</span>
        </div>
      )}

      {/* Busca */}
      <div className="bg-white/10 backdrop-blur-md rounded-xl border border-white/20 p-4">
        <input
          type="text"
          placeholder="Buscar por nome, email ou CPF..."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          className="w-full px-4 py-3 bg-white/10 border border-white/20 rounded-lg text-white placeholder-white/50 focus:outline-none focus:ring-2 focus:ring-blue-500"
        />
      </div>

      {/* Lista de Clientes */}
      <div className="bg-white/10 backdrop-blur-md rounded-xl border border-white/20 overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full">
            <thead className="bg-white/5">
              <tr>
                <th className="px-6 py-4 text-left text-sm font-semibold text-white">
                  Cliente
                </th>
                <th className="px-6 py-4 text-left text-sm font-semibold text-white">
                  Email
                </th>
                <th className="px-6 py-4 text-left text-sm font-semibold text-white">
                  CPF
                </th>
                <th className="px-6 py-4 text-center text-sm font-semibold text-white">
                  Limite de Crédito
                </th>
                <th className="px-6 py-4 text-center text-sm font-semibold text-white">
                  Ações
                </th>
              </tr>
            </thead>
            <tbody className="divide-y divide-white/10">
              {filteredCustomers.length === 0 ? (
                <tr>
                  <td
                    colSpan={5}
                    className="px-6 py-8 text-center text-white/60"
                  >
                    Nenhum cliente encontrado
                  </td>
                </tr>
              ) : (
                filteredCustomers.map((customer) => (
                  <tr
                    key={customer.id}
                    className="hover:bg-white/5 transition-colors"
                  >
                    <td className="px-6 py-4 text-white">{customer.name}</td>
                    <td className="px-6 py-4 text-white/80 text-sm">
                      {customer.emailContact}
                    </td>
                    <td className="px-6 py-4 text-white/80 text-sm">
                      {customer.cpf || "-"}
                    </td>
                    <td className="px-6 py-4">
                      {editingCustomerId === customer.id ? (
                        <div className="flex items-center justify-center gap-2">
                          <span className="text-white">R$</span>
                          <input
                            type="number"
                            value={editingValue}
                            onChange={(e) => setEditingValue(e.target.value)}
                            className="w-32 px-3 py-2 bg-white/10 border border-white/20 rounded-lg text-white text-center focus:outline-none focus:ring-2 focus:ring-blue-500"
                            min="0"
                            step="100"
                            autoFocus
                          />
                        </div>
                      ) : (
                        <div className="text-center">
                          <span
                            className={`text-lg font-semibold ${
                              (customer.creditLimit || 0) > 0
                                ? "text-green-400"
                                : "text-white/40"
                            }`}
                          >
                            {formatCurrency(customer.creditLimit || 0)}
                          </span>
                        </div>
                      )}
                    </td>
                    <td className="px-6 py-4">
                      <div className="flex items-center justify-center gap-2">
                        {editingCustomerId === customer.id ? (
                          <>
                            <button
                              onClick={() => handleSave(customer.id)}
                              disabled={savingCustomerId === customer.id}
                              className="bg-green-600 hover:bg-green-700 text-white p-2 rounded-lg transition-colors disabled:opacity-50"
                              title="Salvar"
                            >
                              {savingCustomerId === customer.id ? (
                                <div className="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin" />
                              ) : (
                                <Save className="w-4 h-4" />
                              )}
                            </button>
                            <button
                              onClick={handleCancelEdit}
                              disabled={savingCustomerId === customer.id}
                              className="bg-gray-600 hover:bg-gray-700 text-white p-2 rounded-lg transition-colors disabled:opacity-50"
                              title="Cancelar"
                            >
                              <X className="w-4 h-4" />
                            </button>
                          </>
                        ) : (
                          <button
                            onClick={() => handleEditClick(customer)}
                            className="bg-blue-600 hover:bg-blue-700 text-white p-2 rounded-lg transition-colors"
                            title="Editar Limite"
                          >
                            <Edit2 className="w-4 h-4" />
                          </button>
                        )}
                      </div>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>

      {/* Informações */}
      <div className="bg-blue-500/20 border border-blue-500/50 rounded-lg p-4">
        <div className="flex items-start gap-3">
          <AlertCircle className="w-5 h-5 text-blue-400 mt-0.5 flex-shrink-0" />
          <div className="text-sm text-blue-200">
            <p className="font-semibold mb-2">
              Como funciona o limite de crédito:
            </p>
            <ul className="list-disc list-inside space-y-1">
              <li>
                Pedidos de aluguel com valor <strong>acima do limite</strong>{" "}
                são automaticamente rejeitados
              </li>
              <li>
                Pedidos <strong>dentro do limite</strong> são enviados para
                análise manual
              </li>
              <li>
                Clientes sem limite definido não podem fazer pedidos de aluguel
                do banco
              </li>
              <li>O limite pode ser ajustado a qualquer momento</li>
            </ul>
          </div>
        </div>
      </div>
    </div>
  );
};

export default CreditLimitManagement;
