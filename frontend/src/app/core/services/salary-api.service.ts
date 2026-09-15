import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  OverallKpis,
  DepartmentAnalytics,
  CountryAnalytics,
  SalaryBandDistribution,
  Employee,
  PaginatedResponse,
  AssistantQueryResponse
} from '../models/salary.model';

export interface CreateEmployeePayload {
  firstName: string;
  lastName: string;
  email: string;
  department: string;
  jobTitle: string;
  country: string;
  city: string;
  currencyCode: string;
  annualBaseSalaryLocal: number;
}

export interface UpdateEmployeePayload {
  department: string;
  jobTitle: string;
  annualBaseSalaryLocal: number;
}

@Injectable({
  providedIn: 'root'
})
export class SalaryApiService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = 'http://localhost:8080/api/v1';

  getKpis(): Observable<OverallKpis> {
    return this.http.get<OverallKpis>(`${this.baseUrl}/analytics/kpis`);
  }

  getDepartmentAnalytics(): Observable<DepartmentAnalytics[]> {
    return this.http.get<DepartmentAnalytics[]>(`${this.baseUrl}/analytics/departments`);
  }

  getCountryAnalytics(): Observable<CountryAnalytics[]> {
    return this.http.get<CountryAnalytics[]>(`${this.baseUrl}/analytics/countries`);
  }

  getSalaryDistribution(): Observable<SalaryBandDistribution[]> {
    return this.http.get<SalaryBandDistribution[]>(`${this.baseUrl}/analytics/distribution`);
  }

  getEmployees(options: {
    page?: number;
    size?: number;
    department?: string;
    country?: string;
    search?: string;
    sort?: string;
  }): Observable<PaginatedResponse<Employee>> {
    let params = new HttpParams()
      .set('page', (options.page ?? 0).toString())
      .set('size', (options.size ?? 20).toString());

    if (options.department) {
      params = params.set('department', options.department);
    }
    if (options.country) {
      params = params.set('country', options.country);
    }
    if (options.search) {
      params = params.set('search', options.search);
    }
    if (options.sort) {
      params = params.set('sort', options.sort);
    }

    return this.http.get<PaginatedResponse<Employee>>(`${this.baseUrl}/employees`, { params });
  }

  createEmployee(payload: CreateEmployeePayload): Observable<Employee> {
    return this.http.post<Employee>(`${this.baseUrl}/employees`, payload);
  }

  updateEmployee(id: number, payload: UpdateEmployeePayload): Observable<Employee> {
    return this.http.put<Employee>(`${this.baseUrl}/employees/${id}`, payload);
  }

  deleteEmployee(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/employees/${id}`);
  }

  getDepartments(): Observable<string[]> {
    return this.http.get<string[]>(`${this.baseUrl}/employees/meta/departments`);
  }

  getCountries(): Observable<string[]> {
    return this.http.get<string[]>(`${this.baseUrl}/employees/meta/countries`);
  }

  askAssistant(question: string): Observable<AssistantQueryResponse> {
    return this.http.post<AssistantQueryResponse>(`${this.baseUrl}/assistant/query`, { question });
  }
}
