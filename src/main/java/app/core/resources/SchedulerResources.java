package app.core.resources;

import app.core.scheduler.Scheduler;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/scheduler")
public class SchedulerResources {

    @Inject
    Scheduler counter;
   @GET
   @Produces(MediaType.TEXT_PLAIN)
    public String getCounter(){
        return "The value of the counter is " + counter;
    }
}
