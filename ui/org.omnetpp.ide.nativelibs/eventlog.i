%module EventLogEngine

// covariant return type warning disabled
#pragma SWIG nowarn=822

%include "loadlib.i"
%include "enumtypeunsafe.swg"
%include "defs.i"
%include "stacktrace.i"
%javaconst(1);

%include "bigdecimal.i"

%{
#include "eventlog/ievent.h"
#include "eventlog/ieventlog.h"
#include "eventlog/event.h"
#include "eventlog/filteredevent.h"
#include "eventlog/messagedependency.h"
#include "eventlog/eventlogentry.h"
#include "eventlog/eventlogentries.h"
#include "eventlog/eventlogindex.h"
#include "eventlog/eventlog.h"
#include "eventlog/eventlogfacade.h"
#include "eventlog/eventlogtablefacade.h"
#include "eventlog/sequencechartfacade.h"
#include "eventlog/filteredeventlog.h"
#include "common/filereader.h"

using namespace omnetpp::eventlog;
%}

// hide export/import macros from swig
#define COMMON_API
#define EVENTLOG_API
#define OPP_DLLEXPORT
#define OPP_DLLIMPORT
#define _OPP_GNU_ATTRIBUTE(x)

%typemap(javacode) omnetpp::common::FileReader %{
    public FileReader(String fileName, boolean cMemoryOwn) {
        this(fileName);
        this.swigCMemOwn = cMemoryOwn;
    }
%}

%include "common/filereader.h"

namespace omnetpp { namespace eventlog {

typedef omnetpp::common::BigDecimal BigDecimal;
typedef omnetpp::common::FileReader FileReader;
typedef omnetpp::common::opp_runtime_error opp_runtime_error;
typedef omnetpp::common::BigDecimal BigDecimal;

typedef uintptr_t omnetpp::eventlog::ptr_t;
typedef int64_t eventnumber_t;
typedef BigDecimal simtime_t;
#define simtime_nil BigDecimal::MinusOne

class BeginSendEntry;
class EndSendEntry;
class EventEntry;
class EventLogEntry;
class EventLogMessageEntry;
class GateCreatedEntry;
class MessageEntry;
class MessageSendDependency;
class ModuleCreatedEntry;
class SimulationBeginEntry;
class IEventLog;
class IMessageDependency;
typedef std::vector<IMessageDependency *> IMessageDependencyList;

} } // namespaces

/*--------------------------------------------------------------------------
 * ptr_t <--> long mapping
 *--------------------------------------------------------------------------*/

%typemap(jni)    omnetpp::eventlog::ptr_t "jlong"
%typemap(jtype)  omnetpp::eventlog::ptr_t "long"
%typemap(jstype) omnetpp::eventlog::ptr_t "long"
%typemap(javain) omnetpp::eventlog::ptr_t "$javainput"
%typemap(javaout) omnetpp::eventlog::ptr_t { return $jnicall; }
%typemap(out) omnetpp::eventlog::ptr_t { $result = (jlong)$1; }
%typemap(in) omnetpp::eventlog::ptr_t { $1 = (omnetpp::eventlog::ptr_t)$input; }

%typemap(jni)    uintptr_t "jlong"
%typemap(jtype)  uintptr_t "long"
%typemap(jstype) uintptr_t "long"
%typemap(javain) uintptr_t "$javainput"
%typemap(javaout) uintptr_t { return $jnicall; }
%typemap(out) uintptr_t { $result = (jlong)$1; }
%typemap(in) uintptr_t { $1 = (uintptr_t)$input; }

%include "std_common.i"
%include "std_string.i"
%include "std_set.i"     // our custom version
%include "std_list.i"    // our custom version
%include "std_vector.i"
%include "std_map.i"

%include "map_oldapi.i" // needed for SWIG >=4.0 so we still have the 3.x functions

namespace std {

   %typemap(javacode) vector<string> %{
       public String[] toArray() {
           int sz = (int) size();
           String[] array = new String[sz];
              for (int i=0; i<sz; i++)
               array[i] = get(i);
           return array;
       }
       public static StringVector fromArray(String[] array) {
           StringVector vector = new StringVector();
           for (int i=0; i<array.length; i++)
               vector.add(array[i]);
           return vector;
       }
   %}

   %typemap(javacode) set<int> %{
       public IntSet(boolean cMemoryOwn) {
           this(EventLogEngineJNI.new_IntSet__SWIG_0(), cMemoryOwn);
       }
   %}

   %extend set<const char *> {
       std::vector<const char *> keys() {
           std::vector<const char *> vec;
           vec.reserve(self->size());
           for (std::set<const char *>::iterator it = self->begin(); it!=self->end(); it++)
               vec.push_back(*it);
           return vec;
       }
   }

   %extend set<string> {
       std::vector<std::string> keys() {
           std::vector<std::string> vec;
           vec.reserve(self->size());
           for (std::set<std::string>::iterator it = self->begin(); it!=self->end(); it++)
               vec.push_back(*it);
           return vec;
       }
   }

   %extend map<string,string> {
       std::vector<std::string> keys() {
           std::vector<std::string> vec;
           vec.reserve(self->size());
           for (std::map<std::string,std::string>::iterator it = self->begin(); it!=self->end(); it++)
               vec.push_back(it->first);
           return vec;
       }
   }

   %template(PStringSet) set<const char *>;
   %template(StringSet) set<string>;
   %template(StringVector) vector<string>;
   %template(PStringVector) vector<const char *>;

   //specialize_std_map_on_both(string,,,,string,,,);
   //%template(StringMap) map<string,string>;

   %template(ModuleCreatedEntryList) vector<omnetpp::eventlog::ModuleCreatedEntry*>;
   %template(IMessageDependencyList) vector<omnetpp::eventlog::IMessageDependency*>;
   %template(MessageSendDependencyList) vector<omnetpp::eventlog::MessageSendDependency*>;
   %template(MessageReuseDependencyList) vector<omnetpp::eventlog::MessageReuseDependency*>;
   %template(FilteredMessageDependencyList) vector<omnetpp::eventlog::FilteredMessageDependency*>;
   %template(MessageEntryList) vector<omnetpp::eventlog::MessageEntry*>;

   %template(IntSet) set<int>;
   %template(PtrSet) set<omnetpp::eventlog::ptr_t>;

   specialize_std_map_on_both(int,,,,int,,,);
   %template(IntIntMap) map<int,int>;

   %template(IntVector) vector<int>;
   %template(LongVector) vector<long>;
   %template(PtrVector) vector<omnetpp::eventlog::ptr_t>;
};

namespace omnetpp { namespace eventlog {

%ignore EventLog::writeTrace;

%define FIX_STRING_MEMBER(STRUCT,MEMBER,CAPITALIZEDMEMBER)
%ignore STRUCT::MEMBER;
%extend STRUCT {
   std::string get ## CAPITALIZEDMEMBER() {return self->MEMBER;}
   void set ## CAPITALIZEDMEMBER(std::string __a) {self->MEMBER = __a;}
}
%enddef

%define FIX_CHARPTR_MEMBER(STRUCT,MEMBER,CAPITALIZEDMEMBER)
%ignore STRUCT::MEMBER;
%extend STRUCT {
   const char * get ## CAPITALIZEDMEMBER() {return self->MEMBER;}
}
%enddef

%{
   JNIEnv *progressDelegateJenv;

   std::string fromJavaString(JNIEnv *jenv, jstring stringObject)
   {
      if (!stringObject)
        return "<null>";
      jboolean isCopy;
      const char *buf = jenv->GetStringUTFChars(stringObject, &isCopy);
      std::string str = buf ? buf : "";
      jenv->ReleaseStringUTFChars(stringObject, buf);
      return str;
   }

   void delegateProgressToJava(IEventLog *eventLog, void *data)
   {
      JNIEnv *jenv = progressDelegateJenv;
      jobject object = (jobject)data;
      jclass clazz = jenv->GetObjectClass(object);
      jmethodID progressMethod = jenv->GetMethodID(clazz, "progress", "()V");
      jenv->ExceptionClear();
      jenv->CallVoidMethod(object, progressMethod);

       jthrowable exceptionObject = jenv->ExceptionOccurred();
       if (exceptionObject)
       {
          jenv->ExceptionDescribe();
          jenv->ExceptionClear();

          jclass throwableClass = jenv->GetObjectClass(exceptionObject);
          jmethodID method = jenv->GetMethodID(throwableClass, "toString", "()Ljava/lang/String;");
          jstring msg = (jstring)jenv->CallObjectMethod(exceptionObject, method);
          throw opp_runtime_error(fromJavaString(jenv, msg).c_str());
      }
   }
%}

%extend IEventLog {
   void setJavaProgressMonitor(JNIEnv *jenv, jobject object) {
      progressDelegateJenv = jenv;
      ProgressMonitor newProgressMonitor(object ? &delegateProgressToJava : NULL, object ? jenv->NewGlobalRef(object) : NULL);
      ProgressMonitor oldProgressMonitor = self->setProgressMonitor(newProgressMonitor);
      jobject oldObject = (jobject)oldProgressMonitor.data;
      if (oldObject)
         jenv->DeleteGlobalRef(oldObject);
   }
}

%typemap(in) JNIEnv * {
  $1 = jenv;
}

%typemap(javaout) EventLogEntry * {
   return EventLogEntry.newEventLogEntry($jnicall, $owner);
}

%typemap(javaimports) EventLogEntry %{
import java.lang.reflect.Constructor;
%}

%typemap(javacode) EventLogEntry %{

   @SuppressWarnings("unchecked")
   private static Constructor[] eventLogEntryConstructors = new Constructor[100];

   public long getCPtr() {
      return swigCPtr;
   }

   public boolean equals(Object obj) {
      return (obj instanceof EventLogEntry) && getCPtr(this)==getCPtr((EventLogEntry)obj);
   }

   public int hashCode() {
      return (int)getCPtr(this);
   }

   @SuppressWarnings("unchecked")
   public static EventLogEntry newEventLogEntry(long cPtr, boolean isOwner) {
      try {
         if (cPtr == 0)
            return null;

         int index = EventLogEngineJNI.EventLogEntry_getClassIndex(cPtr, null);
         Constructor constructor = eventLogEntryConstructors[index];

         if (constructor == null)
         {
            String name = "org.omnetpp.eventlog.engine." + EventLogEngineJNI.EventLogEntry_getClassName(cPtr, null);
            Class clazz = Class.forName(name);
            constructor = clazz.getDeclaredConstructor(long.class, boolean.class);
            eventLogEntryConstructors[index] = constructor;
         }

         return (EventLogEntry)constructor.newInstance(cPtr, isOwner);
      }
      catch (Exception e) {
         throw new RuntimeException(e);
      }
   }
%}

%typemap(javaout) IMessageDependency * {
   return IMessageDependency.newIMessageDependency($jnicall, $owner);
}

%typemap(javaimports) IMessageDependency %{
import java.lang.reflect.Constructor;
%}

%typemap(javacode) IMessageDependency %{

   @SuppressWarnings("unchecked")
   private static Constructor[] messageDependencyConstructors = new Constructor[100];

   public long getCPtr() {
       return swigCPtr;
   }

   public boolean equals(Object obj) {
      return (obj instanceof IMessageDependency) && getCPtr(this)==getCPtr((IMessageDependency)obj);
   }

   public int hashCode() {
      return (int)getCPtr(this);
   }

   @SuppressWarnings("unchecked")
   public static IMessageDependency newIMessageDependency(long cPtr, boolean isOwner) {
      try {
         if (cPtr == 0)
            return null;

         int index = EventLogEngineJNI.IMessageDependency_getClassIndex(cPtr, null);
         Constructor constructor = messageDependencyConstructors[index];

         if (constructor == null)
         {
            String name = "org.omnetpp.eventlog.engine." + EventLogEngineJNI.IMessageDependency_getClassName(cPtr, null);
            Class clazz = Class.forName(name);
            constructor = clazz.getDeclaredConstructor(long.class, boolean.class);
            messageDependencyConstructors[index] = constructor;
         }

         return (IMessageDependency)constructor.newInstance(cPtr, isOwner);
      }
      catch (Exception e) {
         throw new RuntimeException(e);
      }
   }
%}

%typemap(javacode) IEventLog %{
    public IEventLog own() {
        swigCMemOwn = true;
        return this;
    }

    public IEventLog disown() {
        swigCMemOwn = false;
        return this;
    }

    public boolean equals(Object obj) {
        return (obj instanceof IEventLog) && getCPtr(this)==getCPtr((IEventLog)obj);
    }
%}

%typemap(javacode) IEvent %{
    public long getCPtr() {
        return swigCPtr;
    }

    public boolean equals(Object obj) {
        return (obj instanceof IEvent) && getCPtr(this)==getCPtr((IEvent)obj);
    }
%}

%newobject SequenceChartFacade::getModuleMethodBeginEntries;
%newobject SequenceChartFacade::getIntersectingMessageDependencies;

} } // namespaces


%ignore eventLogStringPool;
%ignore FILE;
%ignore *::parse;
%ignore *::print(FILE *);
%ignore *::print(FILE *, bool);
%ignore *::print(FILE *, long);
%ignore *::print(FILE *, long, long);
%ignore *::print(FILE *, long, long, bool);
%ignore *::print(FILE *, long, long, bool, bool);
%ignore *::printInitializationLogEntries(FILE *);
%ignore *::printEvents(FILE *);
%ignore *::printEvents(FILE *, long);
%ignore *::printEvents(FILE *, long, long);
%ignore *::printEvents(FILE *, long, long, bool);
%ignore *::printCause(FILE *);
%ignore *::printConsequence(FILE *);
%ignore *::printMiddle(FILE *);
%ignore omnetpp::eventlog::ProgressMonitor::ProgressMonitor(MonitorFunction);
%ignore omnetpp::eventlog::ProgressMonitor::ProgressMonitor(MonitorFunction, void *);
%ignore omnetpp::eventlog::ProgressMonitor::monitorFunction;
%ignore omnetpp::eventlog::ProgressMonitor::data;

namespace omnetpp { namespace eventlog {

FIX_CHARPTR_MEMBER(EventLogMessageEntry, text, Text);
FIX_CHARPTR_MEMBER(SimulationBeginEntry, runId, RunId);
FIX_CHARPTR_MEMBER(BubbleEntry, text, Text);
FIX_CHARPTR_MEMBER(ModuleMethodBeginEntry, method, Method);
FIX_CHARPTR_MEMBER(ModuleCreatedEntry, moduleClassName, ModuleClassName);
FIX_CHARPTR_MEMBER(ModuleCreatedEntry, fullName, FullName);
FIX_CHARPTR_MEMBER(GateCreatedEntry, name, Name);
FIX_CHARPTR_MEMBER(ConnectionDisplayStringChangedEntry, displayString, DisplayString);
FIX_CHARPTR_MEMBER(ModuleDisplayStringChangedEntry, displayString, DisplayString);
FIX_CHARPTR_MEMBER(BeginSendEntry, messageClassName, MessageClassName);
FIX_CHARPTR_MEMBER(BeginSendEntry, messageName, MessageName);
FIX_CHARPTR_MEMBER(BeginSendEntry, detail, Detail);

} } // namespaces

%include "eventlog/enums.h"
%include "eventlog/ievent.h"
%include "eventlog/ieventlog.h"
%include "eventlog/event.h"
%include "eventlog/filteredevent.h"
%include "eventlog/messagedependency.h"
%include "eventlog/eventlogentry.h"
%include "eventlog/eventlogentries.h"
%include "eventlog/eventlogindex.h"
%include "eventlog/eventlog.h"
%include "eventlog/eventlogfacade.h"
%include "eventlog/eventlogtablefacade.h"
%include "eventlog/sequencechartfacade.h"
%include "eventlog/filteredeventlog.h"
