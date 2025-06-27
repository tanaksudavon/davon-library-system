'use client';

import React, { useState } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { userService } from '@/lib/services/user-service';
import { useAuthStore } from '@/lib/store/auth-store';
import { AuthState } from '@/types/auth';
import { authService } from '@/lib/services/auth-service';

const profileSchema = z.object({
    username: z.string().min(3, 'Username must be at least 3 characters'),
    email: z.string().email('Invalid email address'),
    password: z.string().min(6, 'Password must be at least 6 characters').optional().or(z.literal('')),
    confirmPassword: z.string().optional().or(z.literal('')),
}).refine((data) => data.password === data.confirmPassword, {
    message: "Passwords don't match",
    path: ["confirmPassword"],
});

type ProfileFormData = z.infer<typeof profileSchema>;

export function UserProfileEdit(): React.ReactElement | null {
    const [isEditing, setIsEditing] = useState(false);
    const user = useAuthStore((state: AuthState) => state.user);
    const setAuth = useAuthStore((state: AuthState) => state.setAuth);

    const {
        register,
        handleSubmit,
        formState: { errors, isSubmitting },
        setError,
        reset,
    } = useForm<ProfileFormData>({
        resolver: zodResolver(profileSchema),
        defaultValues: {
            username: user?.username || '',
            email: user?.email || '',
            password: '',
            confirmPassword: '',
        },
    });

    const onSubmit = async (data: ProfileFormData) => {
        try {
            if (!user) return;
            
            const updateData: any = { username: data.username, email: data.email };
            if (data.password) {
                updateData.password = data.password;
            }

            const updatedUser = await userService.updateUser(user.id, updateData);
            if (updatedUser) {
                const token = authService.getToken() || '';
                setAuth(updatedUser, token);
                setIsEditing(false);
                reset({
                    username: updatedUser.username,
                    email: updatedUser.email,
                    password: '',
                    confirmPassword: '',
                });
            }
        } catch (error) {
            setError('root', {
                message: 'Failed to update profile. Please try again.',
            });
        }
    };

    if (!user) return null;

    const handleCancel = () => {
        setIsEditing(false);
        reset({
            username: user.username,
            email: user.email,
            password: '',
            confirmPassword: '',
        });
    };

    return (
        <div>
            {isEditing ? (
                <form onSubmit={handleSubmit(onSubmit)}>
                    <div className="row">
                        <div className="col-12 col-md-8 mb-4">
                            <div className="form-group">
                                <label htmlFor="username" className="form-label">Username</label>
                                <input
                                    type="text"
                                    id="username"
                                    {...register('username')}
                                    className="form-control"
                                />
                                {errors.username && (
                                    <p className="text-danger mt-1 text-sm">{errors.username.message}</p>
                                )}
                            </div>
                        </div>

                        <div className="col-12 col-md-8 mb-4">
                            <div className="form-group">
                                <label htmlFor="email" className="form-label">Email</label>
                                <input
                                    type="email"
                                    id="email"
                                    {...register('email')}
                                    className="form-control"
                                />
                                {errors.email && (
                                    <p className="text-danger mt-1 text-sm">{errors.email.message}</p>
                                )}
                            </div>
                        </div>

                        <div className="col-12 col-md-8 mb-4">
                            <div className="form-group">
                                <label htmlFor="password" className="form-label">
                                    New Password (leave blank to keep current)
                                </label>
                                <input
                                    type="password"
                                    id="password"
                                    {...register('password')}
                                    className="form-control"
                                />
                                {errors.password && (
                                    <p className="text-danger mt-1 text-sm">{errors.password.message}</p>
                                )}
                            </div>
                        </div>

                        <div className="col-12 col-md-8 mb-4">
                            <div className="form-group">
                                <label htmlFor="confirmPassword" className="form-label">
                                    Confirm New Password
                                </label>
                                <input
                                    type="password"
                                    id="confirmPassword"
                                    {...register('confirmPassword')}
                                    className="form-control"
                                />
                                {errors.confirmPassword && (
                                    <p className="text-danger mt-1 text-sm">{errors.confirmPassword.message}</p>
                                )}
                            </div>
                        </div>
                    </div>

                    {errors.root && (
                        <div className="alert alert-danger">
                            {errors.root.message}
                        </div>
                    )}

                    <div className="mt-4 flex justify-end space-x-2">
                        <button
                            type="button"
                            onClick={handleCancel}
                            className="btn btn-secondary mr-2"
                        >
                            Cancel
                        </button>
                        <button
                            type="submit"
                            disabled={isSubmitting}
                            className="btn btn-primary"
                        >
                            {isSubmitting ? 'Saving...' : 'Save Changes'}
                        </button>
                    </div>
                </form>
            ) : (
                <div>
                    <div className="row mb-4">
                        <div className="col-12 col-md-8">
                            <div className="form-group">
                                <label className="form-label">Username</label>
                                <div className="form-control">{user.username}</div>
                            </div>
                        </div>
                    </div>

                    <div className="row mb-4">
                        <div className="col-12 col-md-8">
                            <div className="form-group">
                                <label className="form-label">Email</label>
                                <div className="form-control">{user.email}</div>
                            </div>
                        </div>
                    </div>

                    <div className="mt-4">
                        <button
                            type="button"
                            onClick={() => {
                                setIsEditing(true);
                                reset({
                                    username: user.username,
                                    email: user.email,
                                    password: '',
                                    confirmPassword: '',
                                });
                            }}
                            className="btn btn-primary"
                        >
                            Edit Profile
                        </button>
                    </div>
                </div>
            )}
        </div>
    );
} 