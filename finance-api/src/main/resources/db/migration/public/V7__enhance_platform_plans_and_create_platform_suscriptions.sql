ALTER TABLE public.platform_plans
    ADD COLUMN IF NOT EXISTS plan_type VARCHAR(20) NOT NULL DEFAULT 'PAID';

ALTER TABLE public.platform_plans
    ADD COLUMN IF NOT EXISTS trial_days INTEGER NULL;

CREATE TABLE IF NOT EXISTS public.platform_subscriptions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL REFERENCES public.platform_tenants(id) ON DELETE CASCADE,
    plan_id UUID NOT NULL REFERENCES public.platform_plans(id),
    status VARCHAR(30) NOT NULL,
    is_trial BOOLEAN NOT NULL DEFAULT FALSE,
    current_subscription BOOLEAN NOT NULL DEFAULT TRUE,
    started_at TIMESTAMPTZ NOT NULL,
    expires_at TIMESTAMPTZ NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE UNIQUE INDEX IF NOT EXISTS uq_platform_subscriptions_current_per_tenant
    ON public.platform_subscriptions(tenant_id)
    WHERE current_subscription = true;

CREATE INDEX IF NOT EXISTS idx_platform_subscriptions_tenant_id
    ON public.platform_subscriptions(tenant_id);

CREATE INDEX IF NOT EXISTS idx_platform_subscriptions_plan_id
    ON public.platform_subscriptions(plan_id);

CREATE INDEX IF NOT EXISTS idx_platform_subscriptions_status
    ON public.platform_subscriptions(status);
