'use client';

import React, { useState, useEffect } from 'react';
import { reservationService } from '@/lib/api/services/reservation.service';
import { FiX } from 'react-icons/fi';

interface Reservation {
  id: number;
  bookTitle: string;
  bookAuthor: string;
  queuePosition: number;
  reservationDate: string;
  status: 'ACTIVE' | 'READY' | 'EXPIRED';
}

interface ReservationQueueProps {
  userId: number;
}

export default function ReservationQueue({ userId }: ReservationQueueProps) {
  const [reservations, setReservations] = useState<Reservation[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchReservations = async () => {
      try {
        const response = await fetch(`http://localhost:8081/api/reservations/user/${userId}`);
        if (response.ok) {
          const data = await response.json();
          setReservations(data);
        }
      } catch (error) {
        console.error('Failed to fetch reservations:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchReservations();
  }, [userId]);

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString();
  };

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'ACTIVE':
        return 'text-blue-600 bg-blue-100';
      case 'READY':
        return 'text-green-600 bg-green-100';
      case 'FULFILLED':
        return 'text-green-600 bg-green-100';
      case 'EXPIRED':
        return 'text-red-600 bg-red-100';
      case 'CANCELLED':
        return 'text-gray-600 bg-gray-100';
      default:
        return 'text-gray-600 bg-gray-100';
    }
  };

  const getStatusText = (status: string) => {
    switch (status) {
      case 'ACTIVE':
        return 'In Queue';
      case 'READY':
        return 'Ready for Pickup';
      case 'FULFILLED':
        return 'Borrowed';
      case 'EXPIRED':
        return 'Expired';
      case 'CANCELLED':
        return 'Cancelled';
      default:
        return status;
    }
  };

  const getQueueText = (position: number, status: string) => {
    if (status === 'READY') return 'Ready!';
    if (status === 'FULFILLED') return 'Borrowed';
    if (status === 'EXPIRED') return 'Expired';
    if (status === 'CANCELLED') return 'Cancelled';
    if (position === 1) return 'Next in line';
    return `Position ${position}`;
  };

  const handleCancelReservation = async (reservationId: number) => {
    try {
      // Optimistically update the UI first for immediate feedback
      const updatedReservations = reservations.map(reservation => 
        reservation.id === reservationId 
          ? { ...reservation, status: 'CANCELLED' as const }
          : reservation
      );
      setReservations(updatedReservations);

      await reservationService.cancelReservation(reservationId, userId);
      console.log('Reservation cancelled successfully');
      
      // Add a small delay then refresh the reservations list
      setTimeout(async () => {
        const response = await fetch(`http://localhost:8081/api/reservations/user/${userId}`);
        if (response.ok) {
          const data = await response.json();
          setReservations(data);
        }
      }, 500); // 500ms delay
      
    } catch (error) {
      console.error('Failed to cancel reservation:', error);
      // If cancellation failed, reload to restore correct state
      const response = await fetch(`http://localhost:8081/api/reservations/user/${userId}`);
      if (response.ok) {
        const data = await response.json();
        setReservations(data);
      }
    }
  };

  if (loading) {
    return (
      <div className="bg-white rounded-lg shadow-md p-6">
        <h3 className="text-lg font-semibold text-gray-900 mb-4">My Reservations</h3>
        <div className="space-y-4">
          {[...Array(3)].map((_, i) => (
            <div key={i} className="animate-pulse">
              <div className="flex items-center justify-between p-4 border border-gray-200 rounded-lg">
                <div className="flex-1">
                  <div className="h-4 bg-gray-200 rounded w-3/4 mb-2"></div>
                  <div className="h-3 bg-gray-200 rounded w-1/2"></div>
                </div>
                <div className="h-6 bg-gray-200 rounded w-20"></div>
              </div>
            </div>
          ))}
        </div>
      </div>
    );
  }

  return (
    <div className="bg-white rounded-lg shadow-md p-6">
      <h3 className="text-lg font-semibold text-gray-900 mb-4">My Reservations</h3>
      
      {reservations.length === 0 ? (
        <div className="text-center py-8">
          <div className="text-4xl mb-4">📚</div>
          <p className="text-gray-500">No reservations yet</p>
          <p className="text-sm text-gray-400 mt-1">
            Reserve books to see them here
          </p>
        </div>
      ) : (
        <div className="space-y-4">
          {reservations.map((reservation) => (
            <div
              key={reservation.id}
              className="border border-gray-200 rounded-lg p-4 hover:shadow-md transition-shadow"
            >
              <div className="flex items-start justify-between">
                <div className="flex-1">
                  <h4 className="font-medium text-gray-900 mb-1">
                    {reservation.bookTitle}
                  </h4>
                  <p className="text-sm text-gray-600 mb-2">
                    by {reservation.bookAuthor}
                  </p>
                  <p className="text-xs text-gray-500">
                    Reserved on {formatDate(reservation.reservationDate)}
                  </p>
                </div>
                
                <div className="flex flex-col items-end space-y-2">
                  <div className="flex items-center space-x-2">
                    <span
                      className={`px-3 py-1 rounded-full text-xs font-medium ${getStatusColor(
                        reservation.status
                      )}`}
                    >
                      {getStatusText(reservation.status)}
                    </span>
                    
                    {reservation.status === 'ACTIVE' && (
                      <button
                        onClick={() => handleCancelReservation(reservation.id)}
                        className="p-1 text-red-500 hover:text-red-700 hover:bg-red-50 rounded transition-colors"
                        title="Cancel Reservation"
                      >
                        <FiX className="w-4 h-4" />
                      </button>
                    )}
                  </div>
                  
                  {reservation.status === 'ACTIVE' && (
                    <div className="text-right">
                      <div className="text-lg font-bold text-blue-600">
                        #{reservation.queuePosition}
                      </div>
                      <div className="text-xs text-gray-500">
                        {getQueueText(reservation.queuePosition, reservation.status)}
                      </div>
                    </div>
                  )}
                  
                  {reservation.status === 'READY' && (
                    <div className="text-right">
                      <div className="text-lg">🎉</div>
                      <div className="text-xs text-green-600 font-medium">
                        Pick up soon!
                      </div>
                    </div>
                  )}
                </div>
              </div>
              
              {reservation.status === 'ACTIVE' && reservation.queuePosition > 1 && (
                <div className="mt-3 pt-3 border-t border-gray-100">
                  <div className="flex items-center text-xs text-gray-500">
                    <div className="flex-1 bg-gray-200 rounded-full h-2 mr-3">
                      <div
                        className="bg-blue-500 h-2 rounded-full transition-all duration-300"
                        style={{
                          width: `${Math.max(10, (1 / reservation.queuePosition) * 100)}%`
                        }}
                      ></div>
                    </div>
                    <span>{reservation.queuePosition - 1} ahead of you</span>
                  </div>
                </div>
              )}
            </div>
          ))}
        </div>
      )}
    </div>
  );
} 