package creativitium.revolution.moderation.services;

import creativitium.revolution.foundation.templates.RService;
import creativitium.revolution.moderation.Moderation;

public class ModerationService extends RService
{
    public ModerationService()
    {
        super(Moderation.getInstance());
    }

    @Override
    public void onStart()
    {
    }

    @Override
    public void onStop()
    {
    }


}
