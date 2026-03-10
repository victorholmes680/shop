import { create } from 'zustand';
import type { Product, ProductFilter } from '../types';
import { productApi } from '../services/api';

interface ProductState {
  products: Product[];
  currentProduct: Product | null;
  categories: string[];
  totalElements: number;
  totalPages: number;
  currentPage: number;
  isLoading: boolean;
  error: string | null;

  fetchProducts: (filters?: ProductFilter) => Promise<void>;
  fetchProduct: (id: number) => Promise<void>;
  fetchCategories: () => Promise<void>;
  clearCurrentProduct: () => void;
  clearError: () => void;
}

export const useProductStore = create<ProductState>((set) => ({
  products: [],
  currentProduct: null,
  categories: [],
  totalElements: 0,
  totalPages: 0,
  currentPage: 0,
  isLoading: false,
  error: null,

  fetchProducts: async (filters?: ProductFilter) => {
    set({ isLoading: true, error: null });
    try {
      const response = await productApi.getProducts(filters);
      set({
        products: response.content,
        totalElements: response.totalElements,
        totalPages: response.totalPages,
        currentPage: response.number,
        isLoading: false,
      });
    } catch (error: any) {
      set({
        error: error.message || 'Failed to fetch products',
        isLoading: false,
      });
    }
  },

  fetchProduct: async (id: number) => {
    set({ isLoading: true, error: null });
    try {
      const product = await productApi.getProduct(id);
      set({ currentProduct: product, isLoading: false });
    } catch (error: any) {
      set({
        error: error.message || 'Failed to fetch product',
        isLoading: false,
      });
    }
  },

  fetchCategories: async () => {
    try {
      const categories = await productApi.getCategories();
      set({ categories });
    } catch (error: any) {
      set({ error: error.message || 'Failed to fetch categories' });
    }
  },

  clearCurrentProduct: () => set({ currentProduct: null }),
  clearError: () => set({ error: null }),
}));
