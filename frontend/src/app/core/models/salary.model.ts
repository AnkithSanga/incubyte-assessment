export interface OverallKpis {
  totalEmployees: number;
  totalPayrollUsd: number;
  averageSalaryUsd: number;
  medianSalaryUsd: number;
  minSalaryUsd: number;
  maxSalaryUsd: number;
  highestPayingDepartment: string;
  highestPayingCountry: string;
}

export interface DepartmentAnalytics {
  department: string;
  headcount: number;
  totalPayrollUsd: number;
  averageSalaryUsd: number;
  minSalaryUsd: number;
  maxSalaryUsd: number;
  payrollPercentage: number;
}

export interface CountryAnalytics {
  country: string;
  currencyCode: string;
  headcount: number;
  totalPayrollUsd: number;
  averageSalaryUsd: number;
  minSalaryUsd: number;
  maxSalaryUsd: number;
  payrollPercentage: number;
}

export interface SalaryBandDistribution {
  label: string;
  minUsd: number;
  maxUsd: number;
  count: number;
  percentage: number;
}

export interface Employee {
  id: number;
  employeeId: string;
  firstName: string;
  lastName: string;
  fullName: string;
  email: string;
  department: string;
  jobTitle: string;
  country: string;
  city: string;
  currencyCode: string;
  annualBaseSalaryLocal: number;
  annualBaseSalaryUsd: number;
}

export interface PageMetadata {
  size: number;
  number: number;
  totalElements: number;
  totalPages: number;
}

export interface PaginatedResponse<T> {
  content: T[];
  page?: PageMetadata;
  totalElements?: number;
  totalPages?: number;
  number?: number;
  size?: number;
}

export interface AssistantQueryResponse {
  query: string;
  answer: string;
  category: string;
  dataPoints: Record<string, any>;
}
