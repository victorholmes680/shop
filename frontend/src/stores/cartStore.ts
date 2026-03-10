import { create } from 'zustand';
import type { Cart } from '../types';
import { cartApi } from '../services/api';

interface CartState {
  cart: Cart | null;
  isLoading: boolean;
  error: string | null;
  isOpen: boolean;

  fetchCart: () => Promise<void>;
  addToCart: (productId: number, quantity: number) => Promise<void>;
  updateQuantity: (itemId: number, quantity: number) => Promise<void>;
  removeItem: (itemId: number) => Promise<void>;
  clearCart: () => Promise<void>;
  toggleCart: () => void;
  closeCart: () => void;
  clearError: () => void;
}

export const useCartStore = create<CartState>((set, get) => ({
  cart: null,
  isLoading: false,
  error: null,
  isOpen: false,

  fetchCart: async () => {
    set({ isLoading: true, error: null });
    try {
      const cart = await cartApi.getCart();
      set({ cart, isLoading: false });
    } catch (error: any) {
      set({
        error: error.message || 'Failed to fetch cart',
        isLoading: false,
      });
    }
  },

  addToCart: async (productId: number, quantity: number) => {
    set({ isLoading: true, error: null });
    try {
      const cart = await cartApi.addToCart({ productId, quantity });
      set({ cart, isLoading: true });
      // Open cart to show the item was added
      set({ isOpen: true, isLoading: false });
    } catch (error: any) {
      set({
        error: error.message || 'Failed to add to cart',
        isLoading: false,
      });
      throw error;
    }
  },

  updateQuantity: async (itemId: number, quantity: number) => {
    set({ isLoading: true, error: null });
    try {
      const cart = await cartApi.updateCartItem(itemId, quantity);
      set({ cart, isLoading: false });
    } catch (error: any) {
      set({
        error: error.message || 'Failed to update cart',
        isLoading: false,
      });
    }
  },

  removeItem: async (itemId: number) => {
    set({ isLoading: true, error: null });
    try {
      const cart = await cartApi.removeCartItem(itemId);
      set({ cart, isLoading: false });
    } catch (error: any) {
      set({
        error: error.message || 'Failed to remove item',
        isLoading: false,
      });
    }
  },

  clearCart: async () => {
    set({ isLoading: true, error: null });
    try {
      await cartApi.clearCart();
      set({ cart: null, isLoading: false });
    } catch (error: any) {
      set({
        error: error.message || 'Failed to clear cart',
        isLoading: false,
      });
    }
  },

  toggleCart: () => set({ isOpen: !get().isOpen }),
  closeCart: () => set({ isOpen: false }),
  clearError: () => set({ error: null }),
}));
